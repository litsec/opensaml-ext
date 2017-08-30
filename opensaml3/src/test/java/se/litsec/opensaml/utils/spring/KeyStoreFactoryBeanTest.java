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

import java.io.IOException;
import java.security.KeyStore;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Test cases for {@code KeyStoreFactoryBean}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class KeyStoreFactoryBeanTest {
  
  public static final Resource TRUST_JKS = new ClassPathResource("/spring-utils/trust.jks");
  public static final char[] TRUST_JKS_PASSWORD = "secret".toCharArray();
  
  public static final Resource SAMPLE_P12 = new ClassPathResource("/spring-utils/sample.p12");
  public static final char[] SAMPLE_P12_PASSWORD = "9665545251064808".toCharArray();
  
  public static final Resource CACERTS_JKS = new ClassPathResource("cacerts");
  public static final char[] CACERTS_PASSWORD = "changeit".toCharArray();
  
  @Test
  public void testLoadJKS() throws Exception {
    
    KeyStoreFactoryBean factoryBean = new KeyStoreFactoryBean(TRUST_JKS, TRUST_JKS_PASSWORD); 
    factoryBean.afterPropertiesSet();
    
    KeyStore keyStore = factoryBean.getObject();
    Assert.assertEquals(1, keyStore.size());
    
    factoryBean = new KeyStoreFactoryBean(CACERTS_JKS, CACERTS_PASSWORD); 
    factoryBean.afterPropertiesSet();
    
    keyStore = factoryBean.getObject();
    Assert.assertEquals(93, keyStore.size());
  }

  @Test
  public void testLoadPKCS12() throws Exception {    
    
    KeyStoreFactoryBean factoryBean = new KeyStoreFactoryBean(SAMPLE_P12, SAMPLE_P12_PASSWORD, "pkcs12"); 
    factoryBean.afterPropertiesSet();
    
    KeyStore keyStore = factoryBean.getObject();
    Assert.assertEquals(1, keyStore.size());
    
    Assert.assertTrue(keyStore.entryInstanceOf("1", KeyStore.PrivateKeyEntry.class));
    Assert.assertEquals(2, keyStore.getCertificateChain("1").length);
  }

  @Test
  public void testDestroy() throws Exception {
    KeyStoreFactoryBean factoryBean = new KeyStoreFactoryBean(TRUST_JKS, TRUST_JKS_PASSWORD);
    factoryBean.setSingleton(false);
    factoryBean.afterPropertiesSet();
    
    KeyStore keyStore = factoryBean.getObject();
    Assert.assertEquals(1, keyStore.size());
    
    factoryBean.destroy();
    try {
      keyStore = factoryBean.getObject();
      Assert.fail("Expected exception since passwords have been cleared");
    }
    catch (IOException e) {      
    }
  }
  
}
