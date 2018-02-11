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
