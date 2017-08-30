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
