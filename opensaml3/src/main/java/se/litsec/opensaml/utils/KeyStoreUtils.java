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
    List<X509Certificate> certificates = new ArrayList<X509Certificate>();
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
    return loadKeyStore(locationProperty,
      System.getProperty("javax.net.ssl.trustStorePassword"),
      System.getProperty("javax.net.ssl.trustStoreType"));
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
   */
  public static KeyStore loadKeyStore(String keyStorePath, String keyStorePassword, String keyStoreType) throws KeyStoreException {
    try {
      KeyStore keyStore = keyStoreType != null ? KeyStore.getInstance(keyStoreType) : KeyStore.getInstance(KeyStore.getDefaultType());
      InputStream is = new FileInputStream(keyStorePath);
      keyStore.load(is, keyStorePassword.toCharArray());
      return keyStore;
    }
    catch (IOException | NoSuchAlgorithmException | CertificateException e) {
      throw new KeyStoreException(e);
    }
  }

  // Hidden
  private KeyStoreUtils() {
  }

}
