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
package se.litsec.opensaml.saml2.metadata.build.spring;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.List;

import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.X509Credential;
import org.springframework.core.io.Resource;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.KeyDescriptorBuilder;

/**
 * A Spring factory bean for creating {@link KeyDescriptor} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see KeyDescriptorBuilder
 */
public class KeyDescriptorFactoryBean extends AbstractSAMLObjectBuilderFactoryBean<KeyDescriptor> {

  /** The builder. */
  private KeyDescriptorBuilder builder;

  /**
   * Constructor.
   */
  public KeyDescriptorFactoryBean() {
    this.builder = KeyDescriptorBuilder.builder();
  }

  /**
   * Assigns the usage type for the key descriptor.
   * 
   * @param usageType
   *          the usage type
   */
  public void setUse(UsageType usageType) {
    this.builder.use(usageType);
  }

  /**
   * Assigns the key name of the {@code KeyInfo} element within the key descriptor.
   * 
   * @param name
   *          the key name
   */
  public void setKeyName(String name) {
    this.builder.keyName(name);
  }

  /**
   * Assigns a certificate to be used as a X.509 data element of the {@code KeyInfo} element within the key descriptor.
   * 
   * @param certificate
   *          the certificate
   */
  public void setCertificate(X509Certificate certificate) {
    this.builder.certificate(certificate);
  }

  /**
   * Assigns a certificate in OpenSAML credential format to be used as a X.509 data element of the {@code KeyInfo}
   * element within the key descriptor.
   * 
   * @param credential
   *          the credential
   */
  public void setCertificate(X509Credential credential) {
    this.builder.certificate(credential);
  }

  /**
   * Assigns a resource to a certificate resource that is to be used as a X.509 data element of the {@code KeyInfo}
   * element within the key descriptor.
   * 
   * @param certificateResource
   *          the certificate resource
   * @throws IOException
   *           if the resource can not be read
   */
  public void setCertificateResource(Resource certificateResource) throws IOException {
    this.builder.certificate(certificateResource.getInputStream());
  }

  /**
   * Assigns a list of encryption methods.
   * <p>
   * Note: the method only accepts algorithm URI:s. If you need to assign other parts of an {@code EncryptionMethod}
   * object you must install the method manually and not via the builder.
   * </p>
   * 
   * @param encryptionMethods
   *          list of algorithms
   */
  public void setEncryptionMethods(List<String> encryptionMethods) {
    this.builder.encryptionMethods(encryptionMethods);
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<KeyDescriptor> builder() {
    return this.builder;
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return KeyDescriptor.class;
  }

}
