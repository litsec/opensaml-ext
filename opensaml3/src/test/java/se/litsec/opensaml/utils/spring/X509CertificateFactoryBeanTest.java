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
package se.litsec.opensaml.utils.spring;

import java.security.cert.X509Certificate;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Test cases for {@code X509CertificateFactoryBean}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class X509CertificateFactoryBeanTest {

  public static final Resource DER_RESOURCE = new ClassPathResource("/spring-utils/testca.crt");
  public static final Resource PEM_RESOURCE = new ClassPathResource("/spring-utils/testca.pem");
  
  @Test
  public void testDER() throws Exception {
    X509CertificateFactoryBean factory = new X509CertificateFactoryBean(DER_RESOURCE);
    factory.afterPropertiesSet();    
    Assert.assertTrue(factory.getObjectType().isAssignableFrom(X509Certificate.class));
    X509Certificate cert = factory.getObject();
    Assert.assertNotNull(cert);
  }
  
  @Test
  public void testPEM() throws Exception {
    X509CertificateFactoryBean factory = new X509CertificateFactoryBean(PEM_RESOURCE);
    factory.afterPropertiesSet();    
    Assert.assertTrue(factory.getObjectType().isAssignableFrom(X509Certificate.class));
    X509Certificate cert = factory.getObject();
    Assert.assertNotNull(cert);
  }  

}
