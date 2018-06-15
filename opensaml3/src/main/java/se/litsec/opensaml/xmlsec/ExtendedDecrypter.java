/*
 * Copyright 2016-2018 Litsec AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package se.litsec.opensaml.xmlsec;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.util.Collection;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.encryption.EncryptionMethod;
import org.apache.xml.security.encryption.XMLCipher;
import org.apache.xml.security.encryption.XMLCipherInput;
import org.apache.xml.security.encryption.XMLEncryptionException;
import org.apache.xml.security.utils.EncryptionConstants;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.Decrypter;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.encryption.support.EncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import com.google.common.base.Strings;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

/**
 * An extension of OpenSAML:s {@link Decrypter} implementation that handles the problem that when using the SunPKCS11
 * crypto provider the OAEPPadding does not work. This implementation supplies an workaround for this problem.
 * <p>
 * See this post on <a href=
 * "https://stackoverflow.com/questions/23844694/bad-padding-exception-rsa-ecb-oaepwithsha-256andmgf1padding-in-pkcs11?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa">
 * Stack overflow</a>.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ExtendedDecrypter extends Decrypter {

  /** Class logger. */
  private final Logger log = LoggerFactory.getLogger(ExtendedDecrypter.class);

  /** Test mode allows us to test the functionality with a soft key. */
  private boolean testMode = false;
  
  /** Key length of decryption keys. */
  private int keyLength = -1;
  
  /** Resolver for key encryption keys. */
  private KeyInfoCredentialResolver _kekResolver;

  /**
   * Constructor.
   * 
   * @param params
   *          decryption parameters to use
   */
  public ExtendedDecrypter(DecryptionParameters params) {
    super(params);
    this._kekResolver = params.getKEKKeyInfoCredentialResolver();
  }

  /**
   * Constructor.
   * 
   * @param newResolver
   *          resolver for data encryption keys.
   * @param newKEKResolver
   *          resolver for key encryption keys.
   * @param newEncKeyResolver
   *          resolver for EncryptedKey elements
   */
  public ExtendedDecrypter(KeyInfoCredentialResolver newResolver, KeyInfoCredentialResolver newKEKResolver,
      EncryptedKeyResolver newEncKeyResolver) {
    super(newResolver, newKEKResolver, newEncKeyResolver);
    this._kekResolver = newKEKResolver;
  }

  /**
   * Constructor.
   * 
   * @param newResolver
   *          resolver for data encryption keys.
   * @param newKEKResolver
   *          resolver for key encryption keys.
   * @param newEncKeyResolver
   *          resolver for EncryptedKey elements
   * @param whitelistAlgos
   *          collection of whitelisted algorithm URIs
   * @param blacklistAlgos
   *          collection of blacklisted algorithm URIs
   */
  public ExtendedDecrypter(KeyInfoCredentialResolver newResolver, KeyInfoCredentialResolver newKEKResolver,
      EncryptedKeyResolver newEncKeyResolver, Collection<String> whitelistAlgos, Collection<String> blacklistAlgos) {
    super(newResolver, newKEKResolver, newEncKeyResolver, whitelistAlgos, blacklistAlgos);
    this._kekResolver = newKEKResolver;
  }
  
  /**
   * Init method for setting key size ...
   */
  public void init() {
    if (this._kekResolver != null) {
      CriteriaSet cs = new CriteriaSet();
      try {
        Credential cred = this._kekResolver.resolveSingle(cs);
        if (cred != null) {
          PublicKey pubKey = cred.getPublicKey();
          if (pubKey != null) {
            this.keyLength = getKeyLength(pubKey);
          }
        }
      }
      catch (ResolverException e) {        
      }
      if (this.keyLength <= 0) {
        log.error("Failed to resolve any certificates for key decryption");
      }
    }
  }

  /**
   * Overrides the {@link Decrypter#decryptKey(EncryptedKey, String, Key)} so that we may handle the unsupported
   * features of the SunPKCS11 provider.
   */
  @Override
  public Key decryptKey(EncryptedKey encryptedKey, String algorithm, Key kek) throws DecryptionException {

    if (!testMode && kek != null && !"sun.security.pkcs11.P11Key$P11PrivateKey".equals(kek.getClass().getName())) {
      return super.decryptKey(encryptedKey, algorithm, kek);
    }
    if (!AlgorithmSupport.isRSAOAEP(encryptedKey.getEncryptionMethod().getAlgorithm())) {
      return super.decryptKey(encryptedKey, algorithm, kek);
    }

    if (Strings.isNullOrEmpty(algorithm)) {
      log.error("Algorithm of encrypted key not supplied, key decryption cannot proceed.");
      throw new DecryptionException("Algorithm of encrypted key not supplied, key decryption cannot proceed.");
    }
    this.validateAlgorithms(encryptedKey);

    try {
      this.checkAndMarshall(encryptedKey);
    }
    catch (DecryptionException e) {
      log.error("Error marshalling EncryptedKey for decryption", e);
      throw e;
    }
    this.preProcessEncryptedKey(encryptedKey, algorithm, kek);

    XMLCipher xmlCipher;
    try {
      if (getJCAProviderName() != null) {
        xmlCipher = XMLCipher.getProviderInstance(getJCAProviderName());
      }
      else {
        xmlCipher = XMLCipher.getInstance();
      }
      xmlCipher.init(XMLCipher.UNWRAP_MODE, kek);
    }
    catch (XMLEncryptionException e) {
      log.error("Error initialzing cipher instance on key decryption", e);
      throw new DecryptionException("Error initialzing cipher instance on key decryption", e);
    }

    org.apache.xml.security.encryption.EncryptedKey encKey;
    try {
      Element targetElement = encryptedKey.getDOM();
      encKey = xmlCipher.loadEncryptedKey(targetElement.getOwnerDocument(), targetElement);
    }
    catch (XMLEncryptionException e) {
      log.error("Error when loading library native encrypted key representation", e);
      throw new DecryptionException("Error when loading library native encrypted key representation", e);
    }

    try {
      Key key = this.customizedDecryptKey(encKey, algorithm, kek);
      if (key == null) {
        throw new DecryptionException("Key could not be decrypted");
      }
      return key;
    }
    catch (XMLEncryptionException e) {
      log.error("Error decrypting encrypted key", e);
      throw new DecryptionException("Error decrypting encrypted key", e);
    }
    catch (Exception e) {
      throw new DecryptionException("Probable runtime exception on decryption:" + e.getMessage(), e);
    }
  }

  /**
   * Performs the actual key decryption.
   * 
   * @param encryptedKey
   *          the encrypted key
   * @param algorithm
   *          the algorithm
   * @param kek
   *          the private key
   * @return a secret key
   * @throws XMLEncryptionException
   *           for errors
   */
  @SuppressWarnings("restriction")
  private Key customizedDecryptKey(org.apache.xml.security.encryption.EncryptedKey encryptedKey, String algorithm, Key kek)
      throws XMLEncryptionException {

    // Obtain the encrypted octets
    byte[] encryptedBytes = (new XMLCipherInput(encryptedKey)).getBytes();

    try {
      String provider = this.getJCAProviderName();
      Cipher c = provider != null ? Cipher.getInstance("RSA/ECB/NoPadding", provider) : Cipher.getInstance("RSA/ECB/NoPadding");

      c.init(Cipher.DECRYPT_MODE, kek);
      byte[] paddedPlainText = c.doFinal(encryptedBytes);

      //int keyLength = this.getKeySize(kek);

      /* Ensure leading zeros not stripped */
      if (paddedPlainText.length < this.keyLength / 8) {
        byte[] tmp = new byte[this.keyLength / 8];
        System.arraycopy(paddedPlainText, 0, tmp, tmp.length - paddedPlainText.length, paddedPlainText.length);
        paddedPlainText = tmp;
      }

      EncryptionMethod encMethod = encryptedKey.getEncryptionMethod();
      OAEPParameterSpec oaepParameters = constructOAEPParameters(encMethod.getAlgorithm(), encMethod.getDigestAlgorithm(),
        encMethod.getMGFAlgorithm(), encMethod.getOAEPparams());

      sun.security.rsa.RSAPadding padding = sun.security.rsa.RSAPadding.getInstance(
        sun.security.rsa.RSAPadding.PAD_OAEP_MGF1, keyLength / 8, new SecureRandom(), oaepParameters);
      byte[] secretKeyBytes = padding.unpad(paddedPlainText);

      String jceKeyAlgorithm = JCEMapper.getJCEKeyAlgorithmFromURI(algorithm);

      return new SecretKeySpec(secretKeyBytes, jceKeyAlgorithm);
    }
    catch (NoSuchAlgorithmException | NoSuchPaddingException | NoSuchProviderException | InvalidKeyException | IllegalBlockSizeException
        | BadPaddingException | InvalidAlgorithmParameterException e) {
      throw new XMLEncryptionException(e);
    }
  }

  /**
   * Construct an OAEPParameterSpec object from the given parameters
   */
  private OAEPParameterSpec constructOAEPParameters(
      String encryptionAlgorithm, String digestAlgorithm, String mgfAlgorithm, byte[] oaepParams) {

    String jceDigestAlgorithm = "SHA-1";
    if (digestAlgorithm != null) {
      jceDigestAlgorithm = JCEMapper.translateURItoJCEID(digestAlgorithm);
    }

    PSource.PSpecified pSource = PSource.PSpecified.DEFAULT;
    if (oaepParams != null) {
      pSource = new PSource.PSpecified(oaepParams);
    }

    MGF1ParameterSpec mgfParameterSpec = new MGF1ParameterSpec("SHA-1");
    if (XMLCipher.RSA_OAEP_11.equals(encryptionAlgorithm)) {
      if (EncryptionConstants.MGF1_SHA256.equals(mgfAlgorithm)) {
        mgfParameterSpec = new MGF1ParameterSpec("SHA-256");
      }
      else if (EncryptionConstants.MGF1_SHA384.equals(mgfAlgorithm)) {
        mgfParameterSpec = new MGF1ParameterSpec("SHA-384");
      }
      else if (EncryptionConstants.MGF1_SHA512.equals(mgfAlgorithm)) {
        mgfParameterSpec = new MGF1ParameterSpec("SHA-512");
      }
    }
    return new OAEPParameterSpec(jceDigestAlgorithm, "MGF1", mgfParameterSpec, pSource);
  }

  /**
   * Should we run this class in test mode?
   * 
   * @param testMode
   *          test mode flag
   */
  public void setTestMode(boolean testMode) {
    this.testMode = testMode;
  }

  private static int getKeyLength(final PublicKey pk) {
    int len = -1;
    if (pk instanceof RSAPublicKey) {
      final RSAPublicKey rsapub = (RSAPublicKey) pk;
      len = rsapub.getModulus().bitLength();
    }
    else if (pk instanceof ECPublicKey) {
      final ECPublicKey ecpriv = (ECPublicKey) pk;
      final java.security.spec.ECParameterSpec spec = ecpriv.getParams();
      if (spec != null) {
        len = spec.getOrder().bitLength(); // does this really return something we expect?
      }
      else {
        // We support the key, but we don't know the key length
        len = 0;
      }
    }
    else if (pk instanceof DSAPublicKey) {
      final DSAPublicKey dsapub = (DSAPublicKey) pk;
      if (dsapub.getParams() != null) {
        len = dsapub.getParams().getP().bitLength();
      }
      else {
        len = dsapub.getY().bitLength();
      }
    }
    return len;
  }

}
