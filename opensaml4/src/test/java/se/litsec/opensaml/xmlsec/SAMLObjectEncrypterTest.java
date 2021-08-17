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
package se.litsec.opensaml.xmlsec;

import java.security.KeyStore;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.impl.KeyStoreX509CredentialAdapter;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.encryption.support.EncryptionConstants;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.impl.BasicEncryptionConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.saml2.metadata.build.IdpEntityDescriptorBuilder;
import se.litsec.opensaml.saml2.metadata.build.KeyDescriptorBuilder;
import se.litsec.opensaml.saml2.metadata.provider.CompositeMetadataProvider;
import se.litsec.opensaml.saml2.metadata.provider.MetadataProvider;
import se.litsec.opensaml.saml2.metadata.provider.StaticMetadataProvider;
import se.litsec.opensaml.utils.KeyStoreUtils;

/**
 * Test cases for {@link SAMLObjectEncrypter}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SAMLObjectEncrypterTest extends OpenSAMLTestBase {

  private static final String ENTITY_ID = "http://www.example.com/idp";
  private static final String CONTENTS = "This is the encrypted message";

  //
  // Test with default settings and a metadata entry that contains use="encryption".
  //
  @Test
  public void testDefault() throws Exception {

    XSString msg = (XSString) XMLObjectSupport.buildXMLObject(XSString.TYPE_NAME); 
    msg.setValue(CONTENTS);

    // Setup metadata
    //
    EntityDescriptor ed = this.createMetadata(KeyDescriptorBuilder.builder()
      .use(UsageType.ENCRYPTION)
      .certificate(new ClassPathResource("credentials/litsec_auth.crt").getInputStream())
      .build(),
      KeyDescriptorBuilder.builder()
        .use(UsageType.SIGNING)
        .certificate(new ClassPathResource("credentials/litsec_sign.crt").getInputStream())
        .build());
    MetadataProvider provider = this.createMetadataProvider(ed);
    
    SAMLObjectEncrypter encrypter = new SAMLObjectEncrypter(provider);
    EncryptedData encryptedData = encrypter.encrypt(msg, new SAMLObjectEncrypter.Peer(ENTITY_ID));

//    Element e = ObjectUtils.marshall(encryptedData);
//    System.out.println(SerializeSupport.prettyPrintXML(e));

    String decryptedMsg = this.decrypt(encryptedData, new ClassPathResource("credentials/litsec_auth.jks"), "secret",
      "litsec_ab");

    Assert.assertEquals(CONTENTS, decryptedMsg);
  }
  
  //
  // The same as above, but we don't have a metadata provider. Instead we supply the peer metadata
  // in the call to encrypt.
  //
  @Test
  public void testDefaultNoProvider() throws Exception {

    XSString msg = (XSString) XMLObjectSupport.buildXMLObject(XSString.TYPE_NAME);
    msg.setValue(CONTENTS);

    // Setup metadata
    //
    EntityDescriptor ed = this.createMetadata(KeyDescriptorBuilder.builder()
      .use(UsageType.ENCRYPTION)
      .certificate(new ClassPathResource("credentials/litsec_auth.crt").getInputStream())
      .build(),
      KeyDescriptorBuilder.builder()
        .use(UsageType.SIGNING)
        .certificate(new ClassPathResource("credentials/litsec_sign.crt").getInputStream())
        .build());
    
    SAMLObjectEncrypter encrypter = new SAMLObjectEncrypter();
    EncryptedData encryptedData = encrypter.encrypt(msg, new SAMLObjectEncrypter.Peer(ed));

//    Element e = ObjectUtils.marshall(encryptedData);
//    System.out.println(SerializeSupport.prettyPrintXML(e));

    String decryptedMsg = this.decrypt(encryptedData, new ClassPathResource("credentials/litsec_auth.jks"), "secret",
      "litsec_ab");

    Assert.assertEquals(CONTENTS, decryptedMsg);
  }
  
  //
  // Tests that we find a encryption credential even if the metadata doesn't state encryption use.
  //
  @Test
  public void testUnspecifiedUse() throws Exception {

    XSString msg = (XSString) XMLObjectSupport.buildXMLObject(XSString.TYPE_NAME);
    msg.setValue(CONTENTS);

    // Setup metadata
    //
    EntityDescriptor ed = this.createMetadata(KeyDescriptorBuilder.builder()
      .use(UsageType.UNSPECIFIED)
      .certificate(new ClassPathResource("credentials/litsec_auth.crt").getInputStream())
      .build());
    MetadataProvider provider = this.createMetadataProvider(ed);
    
    SAMLObjectEncrypter encrypter = new SAMLObjectEncrypter(provider);
    EncryptedData encryptedData = encrypter.encrypt(msg, new SAMLObjectEncrypter.Peer(ENTITY_ID));

//    Element e = ObjectUtils.marshall(encryptedData);
//    System.out.println(SerializeSupport.prettyPrintXML(e));

    String decryptedMsg = this.decrypt(encryptedData, new ClassPathResource("credentials/litsec_auth.jks"), "secret",
      "litsec_ab");

    Assert.assertEquals(CONTENTS, decryptedMsg);
  }
  
  //
  // Tests that if we don't find any encryption credentials we fail.
  //
  @Test
  public void testNoEncryptionCredentials() throws Exception {
    
    XSString msg = (XSString) XMLObjectSupport.buildXMLObject(XSString.TYPE_NAME);
    msg.setValue(CONTENTS);

    EntityDescriptor ed = this.createMetadata(
      KeyDescriptorBuilder.builder()
        .use(UsageType.SIGNING)
        .certificate(new ClassPathResource("credentials/litsec_sign.crt").getInputStream())
        .build());
    MetadataProvider provider = this.createMetadataProvider(ed);

    SAMLObjectEncrypter encrypter = new SAMLObjectEncrypter(provider);

    try {
      encrypter.encrypt(msg, new SAMLObjectEncrypter.Peer(ENTITY_ID));
      Assert.fail("Expected error - no encryption credentials found");
    }
    catch (EncryptionException e) {
      System.out.println(e.getMessage());
    }
  }
  
  //
  // Test that we look at what the peer specifies about algorithms in its metadata.
  //
  @Test
  public void testPeerCapabilities() throws Exception {

    XSString msg = (XSString) XMLObjectSupport.buildXMLObject(XSString.TYPE_NAME);
    msg.setValue(CONTENTS);

    // Setup metadata
    //
    EntityDescriptor ed = this.createMetadata(KeyDescriptorBuilder.builder()
      .use(UsageType.ENCRYPTION)
      .certificate(new ClassPathResource("credentials/litsec_auth.crt").getInputStream())
      .encryptionMethods(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM, EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15)
      .build(),
      KeyDescriptorBuilder.builder()
        .use(UsageType.SIGNING)
        .certificate(new ClassPathResource("credentials/litsec_sign.crt").getInputStream())
        .build());
    MetadataProvider provider = this.createMetadataProvider(ed);
    
    // RSA 1.5 is black-listed in the default OpenSAML config, so we make our own config.
    BasicEncryptionConfiguration customConfig = DefaultSecurityConfigurationBootstrap.buildDefaultEncryptionConfiguration();
    customConfig.setExcludedAlgorithms(Collections.emptyList());
    
    SAMLObjectEncrypter encrypter = new SAMLObjectEncrypter(provider);
    EncryptedData encryptedData = encrypter.encrypt(msg, new SAMLObjectEncrypter.Peer(ENTITY_ID), customConfig);
    
    Assert.assertEquals(EncryptionConstants.ALGO_ID_BLOCKCIPHER_AES256_GCM, encryptedData.getEncryptionMethod().getAlgorithm());
    Assert.assertEquals(EncryptionConstants.ALGO_ID_KEYTRANSPORT_RSA15, 
      encryptedData.getKeyInfo().getEncryptedKeys().get(0).getEncryptionMethod().getAlgorithm());

//    Element e = ObjectUtils.marshall(encryptedData);
//    System.out.println(SerializeSupport.prettyPrintXML(e));

    String decryptedMsg = this.decrypt(encryptedData, new ClassPathResource("credentials/litsec_auth.jks"), "secret",
      "litsec_ab");

    Assert.assertEquals(CONTENTS, decryptedMsg);
  }
  
  //
  // Test with default settings and several matching keys
  //
  @Test
  public void testSeveralKeys() throws Exception {

    XSString msg = (XSString) XMLObjectSupport.buildXMLObject(XSString.TYPE_NAME);
    msg.setValue(CONTENTS);

    // Setup metadata
    //
    EntityDescriptor ed = this.createMetadata(KeyDescriptorBuilder.builder()
      .use(UsageType.ENCRYPTION)
      .certificate(new ClassPathResource("credentials/litsec_auth.crt").getInputStream())
      .build(),
      KeyDescriptorBuilder.builder()
        .use(UsageType.ENCRYPTION)
        .certificate(new ClassPathResource("credentials/other.crt").getInputStream())
        .build());
    MetadataProvider provider = this.createMetadataProvider(ed);
    
    SAMLObjectEncrypter encrypter = new SAMLObjectEncrypter(provider);
    EncryptedData encryptedData = encrypter.encrypt(msg, new SAMLObjectEncrypter.Peer(ENTITY_ID));

    Element e = XMLObjectSupport.marshall(encryptedData);
    System.out.println(SerializeSupport.prettyPrintXML(e));

    // One should work
    String decryptedMsg = null;
    try {
      decryptedMsg = this.decrypt(encryptedData, new ClassPathResource("credentials/litsec_auth.jks"), "secret",
          "litsec_ab");
    }
    catch (DecryptionException ex) {
      decryptedMsg = this.decrypt(encryptedData, new ClassPathResource("credentials/other.jks"), "secret",
          "Test");
    }

    Assert.assertEquals(CONTENTS, decryptedMsg);
  }  
  
  private String decrypt(EncryptedData encrypted, Resource jks, String password, String alias) throws Exception {
    KeyStore keyStore = KeyStoreUtils.loadKeyStore(jks.getInputStream(), password, "JKS");
    Credential cred = new KeyStoreX509CredentialAdapter(keyStore, alias, password.toCharArray());

    SAMLObjectDecrypter decrypter = new SAMLObjectDecrypter(cred);
    XSString str = decrypter.decrypt(encrypted, XSString.class);
    return str.getValue();
  }  

  private EntityDescriptor createMetadata(KeyDescriptor... descriptors) {
    IdpEntityDescriptorBuilder builder = new IdpEntityDescriptorBuilder();
    return builder.entityID(ENTITY_ID).id("_id123456").keyDescriptors(descriptors).build();
  }

  private MetadataProvider createMetadataProvider(EntityDescriptor... descriptors)
      throws MarshallingException, ComponentInitializationException {
    
    MetadataProvider mp;
    if (descriptors.length == 1) {
      mp = new StaticMetadataProvider(descriptors[0]);
    }
    else {
      mp = new CompositeMetadataProvider("md", Arrays.asList(descriptors).stream().map(d -> {
        try {
          return new StaticMetadataProvider(d);
        }
        catch (MarshallingException e) {
          throw new RuntimeException(e);
        }
      }).collect(Collectors.toList()));
    }
    mp.initialize();
    return mp;
  }
}
