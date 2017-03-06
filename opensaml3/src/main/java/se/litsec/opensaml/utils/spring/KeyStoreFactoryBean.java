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

import java.security.KeyStore;
import java.util.Arrays;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

/**
 * An implementation of a Spring {@code FactoryBean} that reads a keystore file.
 * 
 * @author Martin Lindström (martin@litsec.se)
 */
public class KeyStoreFactoryBean extends AbstractFactoryBean<KeyStore> {

  /** The resource holding the keystore. */
  protected Resource storeLocation;

  /** The keystore password. */
  protected char[] storePassword;

  /** The type of keystore. */
  protected String storeType;

  /**
   * Constructor that takes a resource reference to a JKS-file and the password to unlock this file.
   * 
   * @param storeLocation
   *          path to the JKS-file
   * @param storePassword
   *          the password for unlocking the keystore
   */
  public KeyStoreFactoryBean(Resource storeLocation, char[] storePassword) {
    this(storeLocation, storePassword, KeyStore.getDefaultType());
  }

  /**
   * Constructor that takes a resource reference to a keystore file, the password to unlock this file and the store type
   * ("jks", "pkcs12", ...).
   * 
   * @param storeLocation
   *          path to the keystore file
   * @param storePassword
   *          the password for unlocking the keystore
   * @param storeType
   *          the type of keystore
   */
  public KeyStoreFactoryBean(Resource storeLocation, char[] storePassword, String storeType) {
    this.storeLocation = storeLocation;
    this.storePassword = storePassword != null ? Arrays.copyOf(storePassword, storePassword.length) : new char[0];
    this.storeType = storeType;
  }

  /** {@inheritDoc} */
  @Override
  protected KeyStore createInstance() throws Exception {
    try {
      KeyStore keystore = KeyStore.getInstance(this.storeType);
      keystore.load(this.storeLocation.getInputStream(), this.storePassword);

      return keystore;
    }
    finally {
      if (this.isSingleton()) {
        // We don't want to keep the password in memory longer than needed
        Arrays.fill(this.storePassword, (char) 0);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return KeyStore.class;
  }

  /** {@inheritDoc} */
  @Override
  public void destroy() throws Exception {
    super.destroy();
    Arrays.fill(this.storePassword, (char) 0);
  }

}
