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

import org.opensaml.saml.ext.saml2alg.SigningMethod;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.SigningMethodBuilder;

/**
 * A Spring factory bean for {@link SigningMethod} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)s
 */
public class SigningMethodFactoryBean extends AbstractSAMLObjectBuilderFactoryBean<SigningMethod> {

  /** The builder. */
  private SigningMethodBuilder builder;

  /**
   * Constructor.
   * 
   * @param algorithm
   *          the algorithm
   */
  public SigningMethodFactoryBean(String algorithm) {
    this(algorithm, null, null);
  }

  /**
   * Constructor.
   * 
   * @param algorithm
   *          the algorithm
   * @param minKeySize
   *          the minimum key size (in bits)
   * @param maxKeySize
   *          the maximum key size (in bits)
   */
  public SigningMethodFactoryBean(String algorithm, Integer minKeySize, Integer maxKeySize) {
    this.builder = SigningMethodBuilder.builder();
    this.builder.algorithm(algorithm).minKeySize(minKeySize).maxKeySize(maxKeySize);
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<SigningMethod> builder() {
    return this.builder;
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return SigningMethod.class;
  }

}
