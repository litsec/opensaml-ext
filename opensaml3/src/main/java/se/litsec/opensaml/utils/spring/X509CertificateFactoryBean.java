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

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

/**
 * A factory bean for creating X.509 certificates read from file.
 * 
 * @author Martin Lindström (martin@litsec.se)
 */
public class X509CertificateFactoryBean extends AbstractFactoryBean<X509Certificate> {

  /** The resource holding the certificate. */
  protected Resource resource;

  /** Factory for creating certificates. */
  private static CertificateFactory factory = null;

  static {
    try {
      factory = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException e) {
      throw new SecurityException(e);
    }
  }

  /**
   * Constructor taking a resource/path to a DER- or PEM-encoded certificate.
   * 
   * @param resource
   *          the location of the certificate
   */
  public X509CertificateFactoryBean(Resource resource) {
    this.resource = resource;
  }
  
  /** {@inheritDoc} */
  @Override
  protected X509Certificate createInstance() throws Exception {
    return (X509Certificate) factory.generateCertificate(resource.getInputStream());
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return X509Certificate.class;
  }

}
