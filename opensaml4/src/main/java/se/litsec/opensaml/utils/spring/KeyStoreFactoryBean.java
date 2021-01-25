/*
 * Copyright 2016-2021 Litsec AB
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

import java.security.KeyStore;
import java.util.Arrays;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

/**
 * An implementation of a Spring {@code FactoryBean} that reads a keystore file.
 * 
 * @deprecated use {@code se.swedenconnect.security.credential.factory.KeyStoreFactoryBean} from the credentials-support
 *             library instead
 * 
 * @author Martin Lindström (martin@litsec.se)
 */
@Deprecated(forRemoval = true)
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
