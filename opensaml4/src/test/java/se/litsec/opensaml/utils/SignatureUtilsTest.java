/*
 * Copyright 2016-2021 Litsec AB
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

import java.time.Instant;

import org.apache.xml.security.signature.XMLSignature;
import org.junit.Assert;
import org.junit.Test;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.signature.support.SignatureConstants;
import org.springframework.core.io.ClassPathResource;

import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.saml2.metadata.build.DigestMethodBuilder;
import se.litsec.opensaml.saml2.metadata.build.IdpEntityDescriptorBuilder;
import se.litsec.opensaml.saml2.metadata.build.SigningMethodBuilder;

/**
 * Test cases for the utility methods of {@code SignatureUtils}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SignatureUtilsTest extends OpenSAMLTestBase {

  @Test
  public void testRSAPSS() throws Exception {
    X509Credential rsaCredential = OpenSAMLTestBase.loadKeyStoreCredential(
      new ClassPathResource("rsakey.jks").getInputStream(), "Test1234", "key1", "Test1234");

    EntityDescriptor metadata = IdpEntityDescriptorBuilder.builder()
      .entityID("http://www.dummy.com/idp")
      .digestMethods(false,
        DigestMethodBuilder.builder().algorithm(SignatureConstants.ALGO_ID_DIGEST_SHA384).build())
      .signingMethods(true,
        SigningMethodBuilder.builder().algorithm(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384_MGF1).build())
      .build();

    AuthnRequest authnRequest = getMockAuthnRequest();

    SignatureUtils.sign(authnRequest, rsaCredential,
      SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration(), metadata);

    Assert.assertEquals(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384_MGF1, authnRequest.getSignature().getSignatureAlgorithm());
    Assert.assertTrue(ObjectUtils.toString(authnRequest).contains(
      "<ds:DigestMethod Algorithm=\"" + SignatureConstants.ALGO_ID_DIGEST_SHA384 + "\""));
  }
  
  @Test
  public void testNoPreferences() throws Exception {
    X509Credential rsaCredential = OpenSAMLTestBase.loadKeyStoreCredential(
      new ClassPathResource("rsakey.jks").getInputStream(), "Test1234", "key1", "Test1234");

    AuthnRequest authnRequest = getMockAuthnRequest();

    SignatureUtils.sign(authnRequest, rsaCredential,
      SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration(), null);
    
    // Verify that the default algo is used if the recipient hasn't specified anything
    Assert.assertEquals(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA256, authnRequest.getSignature().getSignatureAlgorithm());
  }
  
  @Test
  public void testECDSA() throws Exception {
    
    X509Credential ecCredential = OpenSAMLTestBase.loadKeyStoreCredential(
      new ClassPathResource("eckey.jks").getInputStream(), "Test1234", "key1", "Test1234");
    
    X509Credential rsaCredential = OpenSAMLTestBase.loadKeyStoreCredential(
      new ClassPathResource("rsakey.jks").getInputStream(), "Test1234", "key1", "Test1234");
    
    EntityDescriptor metadata = IdpEntityDescriptorBuilder.builder()
        .entityID("http://www.dummy.com/idp")
        .signingMethods(true,
          SigningMethodBuilder.builder().algorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512).build(),
          SigningMethodBuilder.builder().algorithm(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512).build())
        .build();

      AuthnRequest authnRequest = getMockAuthnRequest();

      SignatureUtils.sign(authnRequest, ecCredential,
        SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration(), metadata);

      Assert.assertEquals(SignatureConstants.ALGO_ID_SIGNATURE_ECDSA_SHA512, authnRequest.getSignature().getSignatureAlgorithm());
      
      SignatureUtils.sign(authnRequest, rsaCredential,
        SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration(), metadata);

      Assert.assertEquals(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512, authnRequest.getSignature().getSignatureAlgorithm());
  }
  
  @Test
  public void testBadAlgorithms() throws Exception {
    X509Credential rsaCredential = OpenSAMLTestBase.loadKeyStoreCredential(
      new ClassPathResource("rsakey.jks").getInputStream(), "Test1234", "key1", "Test1234");

    EntityDescriptor metadata = IdpEntityDescriptorBuilder.builder()
      .entityID("http://www.dummy.com/idp")
      .digestMethods(false,
        DigestMethodBuilder.builder().algorithm(SignatureConstants.ALGO_ID_SIGNATURE_RSA_SHA512).build(),
        DigestMethodBuilder.builder().algorithm(SignatureConstants.ALGO_ID_DIGEST_SHA384).build())
      .signingMethods(true,
        SigningMethodBuilder.builder().algorithm(SignatureConstants.ALGO_ID_DIGEST_SHA384).build(),
        SigningMethodBuilder.builder().algorithm(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384_MGF1).build())
      .build();

    AuthnRequest authnRequest = getMockAuthnRequest();

    SignatureUtils.sign(authnRequest, rsaCredential,
      SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration(), metadata);

    Assert.assertEquals(XMLSignature.ALGO_ID_SIGNATURE_RSA_SHA384_MGF1, authnRequest.getSignature().getSignatureAlgorithm());
    Assert.assertTrue(ObjectUtils.toString(authnRequest).contains(
      "<ds:DigestMethod Algorithm=\"" + SignatureConstants.ALGO_ID_DIGEST_SHA384 + "\""));
  }  

  /**
   * Creates an {@link AuthnRequest} that we sign.
   * 
   * @return an authentication request object
   */
  private static AuthnRequest getMockAuthnRequest() {
    AuthnRequest authnRequest = (AuthnRequest) XMLObjectSupport.buildXMLObject(AuthnRequest.DEFAULT_ELEMENT_NAME);
    authnRequest.setID("_BmPDpaRGHfHCsqRdeoTHVnsPhNvr3ulQdUoXGgnV");
    authnRequest.setIssueInstant(Instant.now());
    Issuer issuer = (Issuer) XMLObjectSupport.buildXMLObject(Issuer.DEFAULT_ELEMENT_NAME);
    issuer.setFormat(Issuer.ENTITY);
    issuer.setValue("http://www.fake.issuer.com");
    authnRequest.setIssuer(issuer);
    return authnRequest;
  }

}
