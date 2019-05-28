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
package se.litsec.opensaml.utils;

import java.util.Collections;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.impl.BasicSignatureSigningParametersResolver;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

/**
 * Utility methods for signatures.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SignatureUtils {

  /**
   * Signs the supplied SAML object using the credentials.
   * 
   * @param object
   *          object to sign
   * @param signingCredentials
   *          signature credentials
   * @param <T>
   *          the object type
   * @throws SignatureException
   *           for signature creation errors
   */
  public static <T extends SignableSAMLObject> void sign(T object, Credential signingCredentials) throws SignatureException {
    sign(object, signingCredentials, SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration());
  }

  /**
   * Signs the supplied SAML object using the supplied credentials and signature configuration.
   * 
   * @param object
   *          object to sign
   * @param signingCredentials
   *          signature credentials
   * @param config
   *          signature configuration
   * @param <T>
   *          the object type
   * @throws SignatureException
   *           for signature creation errors
   */
  public static <T extends SignableSAMLObject> void sign(T object, Credential signingCredentials, SignatureSigningConfiguration config)
      throws SignatureException {
    
    if (config == null) {
      config = SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration();
    }
    
    try {
      object.setSignature(null);

      BasicSignatureSigningConfiguration signatureCreds = new BasicSignatureSigningConfiguration();
      signatureCreds.setSigningCredentials(Collections.singletonList(signingCredentials));

      BasicSignatureSigningParametersResolver signatureParametersResolver = new BasicSignatureSigningParametersResolver();
      CriteriaSet criteriaSet = new CriteriaSet(new SignatureSigningConfigurationCriterion(config, signatureCreds));

      SignatureSigningParameters parameters = signatureParametersResolver.resolveSingle(criteriaSet);
      SignatureSupport.signObject(object, parameters);
    }
    catch (ResolverException | org.opensaml.security.SecurityException | MarshallingException e) {
      throw new SignatureException(e);
    }
  }

  // Hidden constructor.
  private SignatureUtils() {
  }

}
