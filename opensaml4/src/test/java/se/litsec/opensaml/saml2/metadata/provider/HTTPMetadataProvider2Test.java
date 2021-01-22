/*
 * Copyright 2021 Litsec AB
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
package se.litsec.opensaml.saml2.metadata.provider;

import java.security.cert.X509Certificate;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.ClassPathResource;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.TestWebServer;
import se.litsec.opensaml.utils.KeyStoreUtils;
import se.swedenconnect.security.credential.factory.X509CertificateFactoryBean;

/**
 * Additional test cases for HTTPMetadataProvider.
 * 
 * @author Martin Lindström (martin@litsec.se)
 */
public class HTTPMetadataProvider2Test extends OpenSAMLTestBase {

  @Test
  public void testHttp() throws Exception {
    TestWebServer server = new TestWebServer(() -> new ClassPathResource("/metadata/sveleg-fedtest.xml"), null, null);
    server.start();

    HTTPMetadataProvider provider = null;
    try {
      provider = new HTTPMetadataProvider(server.getUrl(), null);
      provider.setFailFastInitialization(true);
      provider.setRequireValidMetadata(true);
      provider.initialize();

      EntityDescriptor ed = provider.getEntityDescriptor(BaseMetadataProviderTest.TEST_IDP);
      Assert.assertNotNull(String.format("EntityDescriptor for '%s' was not found", BaseMetadataProviderTest.TEST_IDP), ed);
    }
    finally {
      provider.destroy();
      server.stop();
    }
  }

  @Test
  public void testSwedenConnect() throws Exception {
    X509CertificateFactoryBean certFactory = new X509CertificateFactoryBean(new ClassPathResource("sweden-connect-prod.crt"));
    certFactory.afterPropertiesSet();
    X509Certificate signingCert = certFactory.getObject();
    HTTPMetadataProvider provider = null;

    try {
      provider = new HTTPMetadataProvider("https://md.swedenconnect.se/role/idp.xml", null, HTTPMetadataProvider.createDefaultHttpClient(null, null));
      provider.setFailFastInitialization(true);
      provider.setRequireValidMetadata(true);
      provider.setSignatureVerificationCertificate(signingCert);
      provider.initialize();
      
      List<EntityDescriptor> idps = provider.getIdentityProviders();
      Assert.assertTrue(idps.size() > 1);
    }
    finally {
      provider.destroy();
    }
  }
  
  @Test(expected = ComponentInitializationException.class)
  public void testSwedenConnectNotTrusted() throws Exception {
    X509CertificateFactoryBean certFactory = new X509CertificateFactoryBean(new ClassPathResource("sweden-connect-prod.crt"));
    certFactory.afterPropertiesSet();
    X509Certificate signingCert = certFactory.getObject();
    HTTPMetadataProvider provider = null;

    try {
      provider = new HTTPMetadataProvider("https://md.swedenconnect.se/role/idp.xml", null, 
        HTTPMetadataProvider.createDefaultHttpClient(KeyStoreUtils.loadKeyStore("src/test/resources/trust.jks", "secret", null), null));
      provider.setFailFastInitialization(true);
      provider.setRequireValidMetadata(true);
      provider.setSignatureVerificationCertificate(signingCert);
      provider.initialize();
    }
    finally {
      provider.destroy();
    }
  }
  
  @Test(expected = ComponentInitializationException.class)
  public void testSwedenConnectFailedSignatureValidation() throws Exception {
    X509CertificateFactoryBean certFactory = new X509CertificateFactoryBean(new ClassPathResource("spring-utils/testca.crt"));
    certFactory.afterPropertiesSet();
    X509Certificate signingCert = certFactory.getObject();
    HTTPMetadataProvider provider = null;

    try {
      provider = new HTTPMetadataProvider("https://md.swedenconnect.se/role/idp.xml", null, 
        HTTPMetadataProvider.createDefaultHttpClient(null, null));
      provider.setFailFastInitialization(true);
      provider.setRequireValidMetadata(true);
      provider.setSignatureVerificationCertificate(signingCert);
      provider.initialize();
      
      provider.getIdentityProviders();
    }
    finally {
      provider.destroy();
    }
  }

}
