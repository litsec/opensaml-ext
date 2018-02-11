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

import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.UnrecoverableKeyException;
import java.util.Arrays;
import java.util.Enumeration;

import javax.security.auth.DestroyFailedException;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

/**
 * An implementation of a Spring {@code FactoryBean} that reads a JKS-file and extracts a private key with its
 * certificate.
 * 
 * @author Martin Lindström (martin@litsec.se)
 */
public class PrivateKeyEntryFactoryBean extends AbstractFactoryBean<KeyStore.PrivateKeyEntry> {

  /** The resource holding the keystore. */
  protected Resource storeLocation;

  /** The keystore password. */
  protected char[] storePassword;

  /** The type of keystore. */
  protected String storeType;

  /** The alias holding the private key entry. If {@code null} the first private key entry is selected. */
  protected String alias;

  /** The password for unlocking the key. If {@code null} the store password will be used. */
  protected char[] keyPassword;

  /**
   * Constructor setting up a factory bean that will load the JKS-file given by {@code storeLocation}, unlock it and
   * then load the private key entry using the {@code storePassword}.
   * <p>
   * This constructor should be used if the caller knows that the keystore only contains one private key entry that has
   * the same password as the store itself. However, if the keystore contains several private key entries, the first one
   * that can be unlocked using the keystore password will be returned.
   * </p>
   * 
   * @param storeLocation
   *          the path to the JKS-file
   * @param storePassword
   *          the password for unlocking the JKS-file
   * @see #PrivateKeyEntryFactoryBean(Resource, char[], String, char[])
   */
  public PrivateKeyEntryFactoryBean(Resource storeLocation, char[] storePassword) {
    this(storeLocation, storePassword, KeyStore.getDefaultType());
  }

  /**
   * Constructor setting up a factory bean that will load the keystore of the given type from {@code storeLocation},
   * unlock it and then load the private key entry using the {@code storePassword}.
   * <p>
   * This constructor should be used if the caller knows that the keystore only contains one private key entry that has
   * the same password as the store itself. However, if the keystore contains several private key entries, the first one
   * that can be unlocked using the keystore password will be returned.
   * </p>
   * 
   * @param storeLocation
   *          the path to the keystore
   * @param storePassword
   *          the password for unlocking the keystore
   * @param storeType
   *          the type of keystore ("jks", "pkcs12", ...)
   * @see #PrivateKeyEntryFactoryBean(Resource, char[], String, String, char[])
   */
  public PrivateKeyEntryFactoryBean(Resource storeLocation, char[] storePassword, String storeType) {
    this.storeLocation = storeLocation;
    this.storePassword = storePassword != null ? Arrays.copyOf(storePassword, storePassword.length) : new char[0];
    this.storeType = storeType;
  }

  /**
   * Constructor setting up a factory bean the will load a JKS-store, unlock it and locate and unlock the given private
   * key entry identified by {@code alias}.
   * 
   * @param storeLocation
   *          the path to the JKS-file
   * @param storePassword
   *          the password for unlocking the keystore
   * @param alias
   *          keystore alias for the private key entry
   * @param keyPassword
   *          the password for unlocking the private key entry
   */
  public PrivateKeyEntryFactoryBean(Resource storeLocation, char[] storePassword, String alias, char[] keyPassword) {
    this(storeLocation, storePassword, KeyStore.getDefaultType(), alias, keyPassword);
  }

  /**
   * Constructor setting up a factory bean the will load a keystore of the given type, unlock it and locate and unlock
   * the given private key entry identified by {@code alias}.
   * 
   * @param storeLocation
   *          the path to the JKS-file
   * @param storePassword
   *          the password for unlocking the keystore
   * @param storeType
   *          the type of keystore ("jks", "pkcs12", ...)
   * @param alias
   *          keystore alias for the private key entry
   * @param keyPassword
   *          the password for unlocking the private key entry
   */
  public PrivateKeyEntryFactoryBean(Resource storeLocation, char[] storePassword, String storeType, String alias, char[] keyPassword) {
    this(storeLocation, storePassword, storeType);
    this.alias = alias;
    this.keyPassword = keyPassword != null ? Arrays.copyOf(keyPassword, keyPassword.length) : new char[0];
  }

  /** {@inheritDoc} */
  @Override
  protected PrivateKeyEntry createInstance() throws Exception {
    try {
      KeyStore keystore = KeyStore.getInstance(this.storeType);
      keystore.load(this.storeLocation.getInputStream(), this.storePassword);

      if (this.alias != null) {
        return (KeyStore.PrivateKeyEntry) keystore.getEntry(this.alias, new KeyStore.PasswordProtection(this.keyPassword));
      }
      Enumeration<String> aliases = keystore.aliases();
      boolean triedUnlockUsingKeystorePw = false;
      while (aliases.hasMoreElements()) {
        String keyAlias = aliases.nextElement();
        if (keystore.entryInstanceOf(keyAlias, KeyStore.PrivateKeyEntry.class)) {
          char[] pw = this.keyPassword != null ? this.keyPassword : this.storePassword;
          try {
            return (KeyStore.PrivateKeyEntry) keystore.getEntry(keyAlias, new KeyStore.PasswordProtection(pw));
          }
          catch (UnrecoverableKeyException e) {
            if (this.keyPassword == null) {
              triedUnlockUsingKeystorePw = true;
            }
            else {
              throw e;
            }
          }
        }
      }
      if (triedUnlockUsingKeystorePw) {
        throw new GeneralSecurityException("No private key entry found in keystore that could be unlocked with password of keystore");
      }
      else {
        throw new GeneralSecurityException("No private key entry found in keystore");
      }
    }
    finally {
      if (this.isSingleton()) {
        Arrays.fill(this.storePassword, (char) 0);
        if (this.keyPassword != null) {
          Arrays.fill(this.keyPassword, (char) 0);
        }
      }
    }
  }

  /** {@inheritDoc} */
  public Class<? extends KeyStore.PrivateKeyEntry> getObjectType() {
    return KeyStore.PrivateKeyEntry.class;
  }

  /** {@inheritDoc} */
  @Override
  public void destroy() throws Exception {
    super.destroy();

    Arrays.fill(this.storePassword, (char) 0);
    if (this.keyPassword != null) {
      Arrays.fill(this.keyPassword, (char) 0);
    }
  }

  /** {@inheritDoc} */
  @Override
  protected void destroyInstance(PrivateKeyEntry instance) throws Exception {
    if (instance != null && instance.getPrivateKey() != null) {
      try {
        instance.getPrivateKey().destroy();
      }
      catch (DestroyFailedException | SecurityException ignore) {
        // Not all private keys supports destroy
      }
    }
  }

}
