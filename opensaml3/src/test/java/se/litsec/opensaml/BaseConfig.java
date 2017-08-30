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
package se.litsec.opensaml;

import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;

import se.litsec.opensaml.config.OpenSAMLInitializer;

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
      bootstrapper.initialize();
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
