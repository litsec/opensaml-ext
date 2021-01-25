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
package se.litsec.opensaml;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

import se.swedenconnect.opensaml.OpenSAMLInitializer;
import se.swedenconnect.opensaml.OpenSAMLSecurityExtensionConfig;

/**
 * Base Spring configuration file for tests.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
@Configuration
@PropertySource("test-config.properties")
public class BaseConfig {

  /**
   * Singleton bean that initializes the OpenSAML library.
   * 
   * @return {@code OpenSAMLInitializer}
   * @throws Exception
   *           for init errors
   */
  @Bean(name = "openSAMLInitializer")
  public OpenSAMLInitializer openSAMLInitializer() throws Exception {
    OpenSAMLInitializer bootstrapper = OpenSAMLInitializer.getInstance();
    if (!bootstrapper.isInitialized()) {
      bootstrapper.initialize(new OpenSAMLSecurityExtensionConfig());
    }
    return bootstrapper;
  }

  /**
   * Bean for SAML signing certificate used in tests.
   * 
   * @param location
   *          the certificate location
   * @return the signing certificate
   * @throws Exception
   *           for errors
   */
  @Bean(name = "samlSigningCertificate")
  public X509Certificate samlSigningCertificate(@Value("${litsec.saml.signing-cert}") Resource location) throws Exception {
    return this.getCertificate(location);
  }

  /**
   * Bean for SAML encryption certificate used in tests.
   * 
   * @param location
   *          the certificate location
   * @return the encryption certificate
   * @throws Exception
   *           for errors
   */
  @Bean(name = "samlEncryptionCertificate")
  public X509Certificate samlEncryptionCertificate(@Value("${litsec.saml.encryption-cert}") Resource location) throws Exception {
    return this.getCertificate(location);
  }

  private X509Certificate getCertificate(Resource location) throws Exception {
    return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(location.getInputStream());
  }

}
