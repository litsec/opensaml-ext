/*
 * Copyright 2016-2019 Litsec AB
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

import org.opensaml.saml.saml2.metadata.EncryptionMethod;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.EncryptionMethodBuilder;

/**
 * A Spring factory bean for {@link EncryptionMethod} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)s
 */
public class EncryptionMethodFactoryBean extends AbstractSAMLObjectBuilderFactoryBean<EncryptionMethod> {

  /** The builder. */
  private EncryptionMethodBuilder builder;

  /**
   * Constructor.
   * 
   * @param algorithm
   *          the algorithm
   */
  public EncryptionMethodFactoryBean(String algorithm) {
    this(algorithm, null);
  }

  /**
   * Constructor.
   * 
   * @param algorithm
   *          the algorithm
   * @param keySize
   *          the key size (in bits)
   */
  public EncryptionMethodFactoryBean(String algorithm, Integer keySize) {
    this.builder = EncryptionMethodBuilder.builder();
    this.builder.algorithm(algorithm).keySize(keySize);
  }

  /**
   * Assigns the OAEP parameters.
   * 
   * @param base64Encoding
   *          parameters in base64
   */
  public void setOAEPparams(String base64Encoding) {
    this.builder.oAEPparams(base64Encoding);
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<EncryptionMethod> builder() {
    return this.builder;
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return EncryptionMethod.class;
  }

}
