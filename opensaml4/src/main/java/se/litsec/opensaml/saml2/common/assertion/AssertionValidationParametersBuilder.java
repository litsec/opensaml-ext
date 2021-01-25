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
package se.litsec.opensaml.saml2.common.assertion;

import org.opensaml.saml.common.assertion.ValidationContext;

/**
 * Builder class for building the {@link ValidationContext} object for use as validation input to the
 * {@link AssertionValidator}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AssertionValidationParametersBuilder extends AbstractAssertionValidationParametersBuilder<AssertionValidationParametersBuilder> {

  /**
   * Utility method that returns a builder instance.
   * 
   * @return a builder
   */
  public static AssertionValidationParametersBuilder builder() {
    return new AssertionValidationParametersBuilder();
  }  
  
  /** {@inheritDoc} */
  @Override
  protected AssertionValidationParametersBuilder getThis() {
    return this;
  }

}
