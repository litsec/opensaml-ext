/*
 * The opensaml-ext project is an open-source package that extends OpenSAML
 * with useful extensions and utilities.
 *
 * More details on <https://github.com/litsec/opensaml-ext>
 * Copyright (C) 2017 Litsec AB
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package se.litsec.opensaml.xmlsec;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.core.EncryptedElementType;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.DecryptionConfiguration;
import org.opensaml.xmlsec.DecryptionParameters;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.EncryptedKey;
import org.opensaml.xmlsec.encryption.support.Decrypter;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.encryption.support.InlineEncryptedKeyResolver;
import org.opensaml.xmlsec.keyinfo.impl.StaticKeyInfoCredentialResolver;

/**
 * A support bean for easy decryption.
 * <p>
 * For some reason, OpenSAML offers two ways to represent decryption parameters, the {@link DecryptionParameters} and
 * the {@link DecryptionConfiguration}. This bean supports being initialized by either of these two, but also, and
 * perhaps easier to use; it supports initialization with just the encryption credentials and assigns the following
 * defaults:
 * <ul>
 * <li>For the encrypted key resolver an {@link InlineEncryptedKeyResolver} instance is used which which finds
 * {@link EncryptedKey} elements within the {@link org.opensaml.xmlsec.signature.KeyInfo} of the {@link EncryptedData}
 * context.</li>
 * <li>For the key encryption key resolver a {@link StaticKeyInfoCredentialResolver} instance holding the supplied
 * encryption credential(s) is used.</li>
 * </ul>
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SAMLObjectDecrypter {

  /** The decrypter. */
  private Decrypter decrypter;

  /** Decryption parameters. */
  private DecryptionParameters parameters;

  /**
   * Constructor given the credential to use to decrypt the messages (certificate or key pair).
   * 
   * @param decryptionCredential
   *          decryption credential
   */
  public SAMLObjectDecrypter(Credential decryptionCredential) {
    this(Arrays.asList(decryptionCredential));
  }

  /**
   * Constructor accepting several credentials (certificates or key pairs) to be used when decrypting. This may be
   * useful after a key rollover.
   * 
   * @param decryptionCredentials
   *          decryption credentials
   */
  public SAMLObjectDecrypter(List<Credential> decryptionCredentials) {
    this.parameters = new DecryptionParameters();
    this.parameters.setKEKKeyInfoCredentialResolver(new StaticKeyInfoCredentialResolver(decryptionCredentials));
    this.parameters.setEncryptedKeyResolver(new InlineEncryptedKeyResolver());
  }

  /**
   * Initializes the decrypter using {@link DecryptionParameters}.
   * 
   * @param decryptionParameters
   *          parameters
   */
  public SAMLObjectDecrypter(DecryptionParameters decryptionParameters) {
    this.parameters = new DecryptionParameters();
    this.parameters.setDataKeyInfoCredentialResolver(decryptionParameters.getDataKeyInfoCredentialResolver());
    this.parameters.setKEKKeyInfoCredentialResolver(decryptionParameters.getKEKKeyInfoCredentialResolver());
    this.parameters.setEncryptedKeyResolver(decryptionParameters.getEncryptedKeyResolver());
    this.parameters.setBlacklistedAlgorithms(decryptionParameters.getBlacklistedAlgorithms());
    this.parameters.setWhitelistedAlgorithms(decryptionParameters.getWhitelistedAlgorithms());
  }

  /**
   * Initializes the decrypter using {@link DecryptionConfiguration}.
   * 
   * @param decryptionConfiguration
   *          parameters
   */
  public SAMLObjectDecrypter(DecryptionConfiguration decryptionConfiguration) {
    this.parameters = new DecryptionParameters();
    this.parameters.setDataKeyInfoCredentialResolver(decryptionConfiguration.getDataKeyInfoCredentialResolver());
    this.parameters.setKEKKeyInfoCredentialResolver(decryptionConfiguration.getKEKKeyInfoCredentialResolver());
    this.parameters.setEncryptedKeyResolver(decryptionConfiguration.getEncryptedKeyResolver());
    this.parameters.setBlacklistedAlgorithms(decryptionConfiguration.getBlacklistedAlgorithms());
    this.parameters.setWhitelistedAlgorithms(decryptionConfiguration.getWhitelistedAlgorithms());
  }

  /**
   * Decrypts the supplied encrypted object into an object of the given type.
   * 
   * @param encryptedObject
   *          the encrypted object
   * @param destinationClass
   *          the class of the destination object
   * @param <T>
   *          the type of the destination object
   * @param <E>
   *          the type of the encrypted object
   * @return the decrypted element of object T
   * @throws DecryptionException
   *           for decryption errors
   */
  public <T extends XMLObject, E extends EncryptedElementType> T decrypt(E encryptedObject, Class<T> destinationClass)
      throws DecryptionException {

    if (encryptedObject.getEncryptedData() == null) {
      throw new DecryptionException("Object contains no encrypted data");
    }

    XMLObject object = this.getDecrypter().decryptData(encryptedObject.getEncryptedData());
    if (!destinationClass.isInstance(object)) {
      throw new DecryptionException(String.format("Decrypted object can not be cast to %s - is %s", 
        destinationClass.getSimpleName(), object.getClass().getSimpleName()));
    }
    return destinationClass.cast(object);
  }

  /**
   * Returns the decrypter to use.
   * 
   * @return the decrypter
   */
  private synchronized Decrypter getDecrypter() {
    if (this.decrypter == null) {
      this.decrypter = new Decrypter(this.parameters);
    }
    return this.decrypter;
  }

  /**
   * Assigns a list of black listed algorithms
   * 
   * @param blacklistedAlgorithms
   *          non allowed algorithms
   */
  public void setBlacklistedAlgorithms(Collection<String> blacklistedAlgorithms) {
    if (this.decrypter != null) {
      throw new IllegalStateException("Object has already been initialized");
    }
    this.parameters.setBlacklistedAlgorithms(blacklistedAlgorithms);
  }

  /**
   * Assigns a list of white listed algorithms
   * 
   * @param whitelistedAlgorithms
   *          white listed algorithms
   */
  public void setWhitelistedAlgorithms(Collection<String> whitelistedAlgorithms) {
    if (this.decrypter != null) {
      throw new IllegalStateException("Object has already been initialized");
    }
    this.parameters.setWhitelistedAlgorithms(whitelistedAlgorithms);
  }

}
