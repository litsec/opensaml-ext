/*
 * Copyright 2016-2019 Litsec AB
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
package se.litsec.opensaml.xmlsec.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.RSAOAEPParameters;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.signature.support.SignatureConstants;

/**
 * A utility class that returns security configuration objects with a configuration according to SAML2Int, see
 * <a href="https://kantarainitiative.github.io/SAMLprofiles/saml2int.html#_cryptographic_algorithms">section 3.3</a> of
 * "SAML V2.0 Interoperability Deployment Profile V2.0".
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see DefaultSecurityConfigurationBootstrap
 */
public class SAML2IntSecurityConfigurationBootstrap extends DefaultSecurityConfigurationBootstrap {

  /** Hidden constructor. */
  protected SAML2IntSecurityConfigurationBootstrap() {
  }

  /**
   * Build and return a encryption configuration according to SAML2Int.
   * 
   * @return encryption configuration
   */
  public static BasicEncryptionConfiguration buildDefaultEncryptionConfiguration() {

    BasicEncryptionConfiguration config = DefaultSecurityConfigurationBootstrap.buildDefaultEncryptionConfiguration();

    config.setDataEncryptionAlgorithms(Arrays.asList(
      // The order of these is significant.
      EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128_GCM,
      EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM,
      EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192_GCM,
      EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES128,
      EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES192,
      EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256));

    config.setKeyTransportEncryptionAlgorithms(Arrays.asList(
      // The order of the RSA algos is significant.
      EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP,
      EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSAOAEP11,

      // The order of these is not significant.
      EncryptionConstants.ALGO_ID_KEYWRAP_AES128,
      EncryptionConstants.ALGO_ID_KEYWRAP_AES192,
      EncryptionConstants.ALGO_ID_KEYWRAP_AES256,
      EncryptionConstants.ALGO_ID_KEYWRAP_TRIPLEDES));

    config.setRSAOAEPParameters(new RSAOAEPParameters(
      SignatureConstants.ALGO_ID_DIGEST_SHA256,
      EncryptionConstants.ALGO_ID_MGF1_SHA1,
      null));

    return config;
  }

  /**
   * Build and return a signature signing configuration according to SAML2Int.
   * 
   * @return signature configuration
   */
  public static BasicSignatureSigningConfiguration buildDefaultSignatureSigningConfiguration() {

    BasicSignatureSigningConfiguration config = DefaultSecurityConfigurationBootstrap.buildDefaultSignatureSigningConfiguration();

    List<String> blacklist = new ArrayList<>(config.getBlacklistedAlgorithms());
    blacklist.addAll(Arrays.asList(
      SignatureConstants.ALGO_ID_DIGEST_SHA1,
      SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1,
      SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1,
      SignatureConstants.ALGO_ID_MAC_HMAC_SHA1));
    config.setBlacklistedAlgorithms(blacklist);

    List<String> signalgos = new ArrayList<>(config.getSignatureAlgorithms());
    signalgos.remove(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA1);
    signalgos.remove(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA1);
    signalgos.remove(SignatureConstants.ALGO_ID_SIGNATURE_DSA_SHA1);
    signalgos.remove(SignatureConstants.ALGO_ID_MAC_HMAC_SHA1);
    config.setSignatureAlgorithms(signalgos);

    List<String> digestalgos = new ArrayList<>(config.getSignatureReferenceDigestMethods());
    digestalgos.remove(SignatureConstants.ALGO_ID_DIGEST_SHA1);
    config.setSignatureReferenceDigestMethods(digestalgos);

    return config;
  }

}
