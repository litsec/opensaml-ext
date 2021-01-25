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
import java.security.GeneralSecurityException;
import java.security.KeyStore.PrivateKeyEntry;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Test cases for {@code PrivateKeyEntryFactoryBean}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class PrivateKeyEntryFactoryBeanTest {

  public static final Resource TEST_JKS = new ClassPathResource("/spring-utils/test.jks");
  public static final char[] TEST_JKS_PASSWORD = "secret".toCharArray();
  public static final String TEST_JKS_ALIAS = "test";
  public static final char[] TEST_JKS_KEY_PASSWORD = TEST_JKS_PASSWORD;

  public static final Resource TEST2_JKS = new ClassPathResource("/spring-utils/test2.jks");
  public static final char[] TEST2_JKS_PASSWORD = "secret".toCharArray();
  public static final String TEST2_JKS_ALIAS = "test";
  public static final char[] TEST2_JKS_KEY_PASSWORD = "password".toCharArray();

  public static final Resource TEST3_JKS = new ClassPathResource("/spring-utils/test3.jks");
  public static final char[] TEST3_JKS_PASSWORD = "secret".toCharArray();
  public static final String TEST3_JKS_ALIAS1 = "test";
  public static final String TEST3_JKS_ALIAS2 = "test2";
  public static final char[] TEST3_JKS_KEY_PASSWORD1 = TEST3_JKS_PASSWORD;
  public static final char[] TEST3_JKS_KEY_PASSWORD2 = "password2".toCharArray();

  public static final Resource SAMPLE_P12 = new ClassPathResource("/spring-utils/sample.p12");
  public static final char[] SAMPLE_P12_PASSWORD = "9665545251064808".toCharArray();
  public static final String SAMPLE_P12_ALIAS = "1";

  /**
   * Tests reading a simple JKS that has only one key that is protected with the same password as the keystore.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testBasic() throws Exception {
    PrivateKeyEntryFactoryBean factory = new PrivateKeyEntryFactoryBean(TEST_JKS, TEST_JKS_PASSWORD);
    factory.afterPropertiesSet();

    PrivateKeyEntry entry = factory.getObject();
    Assert.assertNotNull(entry);
    Assert.assertNotNull(entry.getCertificate());
    Assert.assertNotNull(entry.getPrivateKey());

    // Do the same and give the alias and key password.
    //
    factory = new PrivateKeyEntryFactoryBean(TEST_JKS, TEST_JKS_PASSWORD, TEST_JKS_ALIAS, TEST_JKS_KEY_PASSWORD);
    factory.afterPropertiesSet();

    PrivateKeyEntry entry2 = factory.getObject();
    Assert.assertNotNull(entry2);
    Assert.assertNotNull(entry2.getCertificate());
    Assert.assertNotNull(entry2.getPrivateKey());
    Assert.assertTrue(entry.getCertificate().equals(entry2.getCertificate()));
  }

  /**
   * Tests reading a JKS where the private key does not have the same password as the keystore.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testBasicNotPossible() throws Exception {

    // Simplified constructor should not be possible.
    //
    PrivateKeyEntryFactoryBean factory = new PrivateKeyEntryFactoryBean(TEST2_JKS, TEST2_JKS_PASSWORD);
    try {
      factory.afterPropertiesSet();
      Assert.fail("Expected GeneralSecurityException since no key password was given for private key");
    }
    catch (GeneralSecurityException expected) {
    }

    // This should work
    //
    factory = new PrivateKeyEntryFactoryBean(TEST2_JKS, TEST2_JKS_PASSWORD, TEST2_JKS_ALIAS, TEST2_JKS_KEY_PASSWORD);
    factory.afterPropertiesSet();

    PrivateKeyEntry entry = factory.getObject();
    Assert.assertNotNull(entry);
    Assert.assertNotNull(entry.getCertificate());
    Assert.assertNotNull(entry.getPrivateKey());
  }

  /**
   * Test for a JKS that contains several entries.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testSeveralKeyEntries() throws Exception {

    // One entry uses the same password as the keystore, so this should work.
    //
    PrivateKeyEntryFactoryBean factory = new PrivateKeyEntryFactoryBean(TEST3_JKS, TEST3_JKS_PASSWORD);
    factory.afterPropertiesSet();

    PrivateKeyEntry entry = factory.getObject();
    Assert.assertNotNull(entry);
    Assert.assertNotNull(entry.getCertificate());
    Assert.assertNotNull(entry.getPrivateKey());

    // Do the same but specify alias and pw.
    //
    factory = new PrivateKeyEntryFactoryBean(TEST3_JKS, TEST3_JKS_PASSWORD, TEST3_JKS_ALIAS1, TEST3_JKS_KEY_PASSWORD1);
    factory.afterPropertiesSet();

    PrivateKeyEntry entry2 = factory.getObject();
    Assert.assertNotNull(entry2);
    Assert.assertNotNull(entry2.getCertificate());
    Assert.assertNotNull(entry2.getPrivateKey());
    Assert.assertTrue(entry.getCertificate().equals(entry2.getCertificate()));
    Assert.assertTrue(entry.getPrivateKey().equals(entry2.getPrivateKey()));

    // Now, get the other entry for which we need to give alias and key password
    //
    factory = new PrivateKeyEntryFactoryBean(TEST3_JKS, TEST3_JKS_PASSWORD, TEST3_JKS_ALIAS2, TEST3_JKS_KEY_PASSWORD2);
    factory.afterPropertiesSet();

    PrivateKeyEntry entry3 = factory.getObject();
    Assert.assertNotNull(entry3);
    Assert.assertNotNull(entry3.getCertificate());
    Assert.assertNotNull(entry3.getPrivateKey());
    Assert.assertFalse(entry.getCertificate().equals(entry3.getCertificate()));
    Assert.assertFalse(entry.getPrivateKey().equals(entry3.getPrivateKey()));
  }

  /**
   * Test supplying a PKCS#12 file.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testPKCS12() throws Exception {
    
    // Basic invocation.
    //
    PrivateKeyEntryFactoryBean factory = new PrivateKeyEntryFactoryBean(SAMPLE_P12, SAMPLE_P12_PASSWORD, "pkcs12");
    factory.afterPropertiesSet();

    PrivateKeyEntry entry = factory.getObject();
    Assert.assertNotNull(entry);
    Assert.assertNotNull(entry.getCertificate());
    Assert.assertNotNull(entry.getPrivateKey());

    // Do the same and give the alias and key password.
    //
    factory = new PrivateKeyEntryFactoryBean(SAMPLE_P12, SAMPLE_P12_PASSWORD, "pkcs12", SAMPLE_P12_ALIAS, SAMPLE_P12_PASSWORD);
    factory.afterPropertiesSet();

    PrivateKeyEntry entry2 = factory.getObject();
    Assert.assertNotNull(entry2);
    Assert.assertNotNull(entry2.getCertificate());
    Assert.assertNotNull(entry2.getPrivateKey());
    Assert.assertTrue(entry.getCertificate().equals(entry2.getCertificate()));
  }

  /**
   * Makes sure that the passwords and private key is cleared when the destroy method is called.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testDestroy() throws Exception {
    PrivateKeyEntryFactoryBean factory = new PrivateKeyEntryFactoryBean(TEST_JKS, TEST_JKS_PASSWORD, TEST_JKS_ALIAS, TEST_JKS_KEY_PASSWORD);
    factory.setSingleton(false);
    factory.afterPropertiesSet();

    PrivateKeyEntry entry = factory.getObject();
    Assert.assertNotNull(entry);
    Assert.assertNotNull(entry.getCertificate());
    Assert.assertNotNull(entry.getPrivateKey());

    factory.destroy();

    try {
      entry = factory.getObject();
      Assert.fail("Expected exception since passwords have been cleared");
    }
    catch (IOException expected) {
    }
  }
}
