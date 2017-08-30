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
