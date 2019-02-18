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
package se.litsec.opensaml.saml2.metadata.build;

import org.opensaml.saml.ext.saml2alg.SigningMethod;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;

/**
 * A builder for {@code alg:SigningMethod} elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SigningMethodBuilder extends AbstractSAMLObjectBuilder<SigningMethod> {

  /**
   * Creates a new {@code SigningMethodBuilder} instance.
   * 
   * @return a {@code SigningMethodBuilder} instance
   */
  public static SigningMethodBuilder builder() {
    return new SigningMethodBuilder();
  }

  /**
   * Utility method that creates a {@code SigningMethod} element having only its {@code Algorithm} attribute assigned.
   * 
   * @param algorithm
   *          the algorithm
   * @return a {@code SigningMethod} instance
   */
  public static SigningMethod signingMethod(String algorithm) {
    return builder().algorithm(algorithm).build();
  }

  /**
   * Utility method that creates a {@code SigningMethod} element.
   * 
   * @param algorithm
   *          the algorithm
   * @param minKeySize
   *          minimum key size (in bits)
   * @param maxKeySize
   *          maximum key size (in bits)
   * @return a {@code SigningMethod} instance
   */
  public static SigningMethod signingMethod(String algorithm, Integer minKeySize, Integer maxKeySize) {
    return builder()
      .algorithm(algorithm)
      .minKeySize(minKeySize)
      .maxKeySize(maxKeySize)
      .build();
  }

  /** {@inheritDoc} */
  @Override
  protected Class<SigningMethod> getObjectType() {
    return SigningMethod.class;
  }

  /**
   * Assigns the algorithm.
   * 
   * @param algorithm
   *          the algorithm
   * @return the builder
   */
  public SigningMethodBuilder algorithm(String algorithm) {
    this.object().setAlgorithm(algorithm);
    return this;
  }

  /**
   * Assigns the minimum size in bits for the key.
   * 
   * @param keySize
   *          minimum key size
   * @return the builder
   */
  public SigningMethodBuilder minKeySize(Integer keySize) {
    this.object().setMinKeySize(keySize);
    return this;
  }

  /**
   * Assigns the maximum size in bits for the key.
   * 
   * @param keySize
   *          maximum key size
   * @return the builder
   */
  public SigningMethodBuilder maxKeySize(Integer keySize) {
    this.object().setMaxKeySize(keySize);
    return this;
  }

}
