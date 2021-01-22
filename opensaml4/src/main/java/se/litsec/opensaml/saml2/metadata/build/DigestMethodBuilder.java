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

import org.opensaml.saml.ext.saml2alg.DigestMethod;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;

/**
 * A builder for {@code alg:DigestMethod} elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class DigestMethodBuilder extends AbstractSAMLObjectBuilder<DigestMethod> {

  /**
   * Creates a new {@code DigestMethodBuilder} instance.
   * 
   * @return a {@code SigningMethodBuilder} instance
   */
  public static DigestMethodBuilder builder() {
    return new DigestMethodBuilder();
  }

  /**
   * Utility method that creates a {@code DigestMethod}.
   * 
   * @param algorithm
   *          the algorithm
   * @return a {@code DigestMethod} instance
   */
  public static DigestMethod digestMethod(String algorithm) {
    return builder().algorithm(algorithm).build();
  }

  /** {@inheritDoc} */
  @Override
  protected Class<DigestMethod> getObjectType() {
    return DigestMethod.class;
  }

  /**
   * Assigns the algorithm.
   * 
   * @param algorithm
   *          the algorithm
   * @return the builder
   */
  public DigestMethodBuilder algorithm(String algorithm) {
    this.object().setAlgorithm(algorithm);
    return this;
  }

}
