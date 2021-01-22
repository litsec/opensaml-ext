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

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

/**
 * A factory bean for creating X.509 certificates read from file.
 * 
 * * @deprecated use {@link se.swedenconnect.security.credential.factory.X509CertificateFactoryBean} from the
 * credentials-support library instead
 * 
 * @author Martin Lindström (martin@litsec.se)
 */
@Deprecated(forRemoval = true)
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
