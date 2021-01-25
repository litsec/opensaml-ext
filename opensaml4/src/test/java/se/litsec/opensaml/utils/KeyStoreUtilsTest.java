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
package se.litsec.opensaml.utils;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Test cases for the {@link KeyStoreUtils} class.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class KeyStoreUtilsTest {

  @Test
  public void testLoadSystemTrustStore() throws Exception {

    SystemPropertiesRestorer props = new SystemPropertiesRestorer(
      "javax.net.ssl.trustStore", "javax.net.ssl.trustStorePassword", "javax.net.ssl.trustStoreType");

    try {
      System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
      System.clearProperty("javax.net.ssl.trustStoreType");

      // First, key store isn't there
      System.setProperty("javax.net.ssl.trustStore", "notthere.jks");
      try {
        KeyStoreUtils.loadSystemTrustStore();
        Assert.fail("Expected exception since keystore does not exist");
      }
      catch (KeyStoreException ignore) {
      }

      System.setProperty("javax.net.ssl.trustStore", this.getCaCertsPath());
      KeyStore keystore = KeyStoreUtils.loadSystemTrustStore();
      Assert.assertEquals(93, KeyStoreUtils.getCertificateEntries(keystore).size());
      
      System.setProperty("javax.net.ssl.trustStoreType", "jks");
      keystore = KeyStoreUtils.loadSystemTrustStore();
      Assert.assertEquals(93, KeyStoreUtils.getCertificateEntries(keystore).size());      
    }
    finally {
      props.restore();
    }
  }

  private String getCaCertsPath() throws IOException {
    Resource r = new ClassPathResource("/cacerts");
    return r.getFile().getAbsolutePath();
  }

  private static class SystemPropertiesRestorer {

    Map<String, String> map = new HashMap<String, String>();

    public SystemPropertiesRestorer(String... keys) {
      for (String key : keys) {
        this.map.put(key, System.getProperty(key));
      }
    }

    public void restore() {
      for (Map.Entry<String, String> e : this.map.entrySet()) {
        if (e.getValue() == null) {
          System.clearProperty(e.getKey());
        }
        else {
          System.setProperty(e.getKey(), e.getValue());
        }
      }
    }
  }

}
