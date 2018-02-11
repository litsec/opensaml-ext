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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Utility methods for Java {@link KeyStore} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class KeyStoreUtils {

  /**
   * Returns a list of all certificate entries of the supplied keystore.
   * 
   * @param keyStore
   *          the keystore to read from
   * @return a list of certificates
   * @throws KeyStoreException
   *           for keystore access errors
   */
  public static List<X509Certificate> getCertificateEntries(KeyStore keyStore) throws KeyStoreException {
    List<X509Certificate> certificates = new ArrayList<>();
    Enumeration<String> e = keyStore.aliases();
    while (e.hasMoreElements()) {
      String alias = e.nextElement();
      if (keyStore.isCertificateEntry(alias)) {
        certificates.add((X509Certificate) keyStore.getCertificate(alias));
      }
    }
    return certificates;
  }

  /**
   * Loads a {@link KeyStore} instance that contains the trusted certificates that the running system has configured.
   * using the following system properties:
   * <ul>
   * <li>{@code javax.net.ssl.trustStore} - the path to the keystore holding the system trusted certificates.</li>
   * <li>{@code javax.net.ssl.trustStorePassword} - the password to unlock the keystore.</li>
   * <li>{@code javax.net.ssl.trustStoreType} - the type of the keystore. If this system property is not set the default
   * keystore type will be assumed.</li>
   * </ul>
   * 
   * @return a {@code KeyStore} instance holding the system's trusted certificates
   * @throws KeyStoreException
   *           for errors loading the system trust keystore
   */
  public static KeyStore loadSystemTrustStore() throws KeyStoreException {
    String locationProperty = System.getProperty("javax.net.ssl.trustStore");
    if (locationProperty == null) {
      throw new KeyStoreException("System property 'javax.net.ssl.trustStore' was not set - can not load system trust store");
    }
    try {
      return loadKeyStore(locationProperty,
        System.getProperty("javax.net.ssl.trustStorePassword"),
        System.getProperty("javax.net.ssl.trustStoreType"));
    }
    catch (IOException e) {
      throw new KeyStoreException(e);
    }
  }

  /**
   * Loads a {@link KeyStore} based on the given arguments.
   * 
   * @param keyStorePath
   *          the path to the key store
   * @param keyStorePassword
   *          the key store password
   * @param keyStoreType
   *          the type of the keystore (if {@code null} the default keystore type will be assumed)
   * @return a {@code KeyStore} instance
   * @throws KeyStoreException
   *           for errors loading the keystore
   * @throws IOException for IO errors
   */
  public static KeyStore loadKeyStore(String keyStorePath, String keyStorePassword, String keyStoreType) throws KeyStoreException, IOException {
    return loadKeyStore(new FileInputStream(keyStorePath), keyStorePassword, keyStoreType);
  }

  public static KeyStore loadKeyStore(InputStream keyStoreStream, String keyStorePassword, String keyStoreType) throws KeyStoreException, IOException {
    try {
      KeyStore keyStore = keyStoreType != null ? KeyStore.getInstance(keyStoreType) : KeyStore.getInstance(KeyStore.getDefaultType());
      keyStore.load(keyStoreStream, keyStorePassword.toCharArray());
      return keyStore;
    }
    catch (NoSuchAlgorithmException | CertificateException e) {
      throw new KeyStoreException(e);
    }
  }

  
  // Hidden
  private KeyStoreUtils() {
  }

}
