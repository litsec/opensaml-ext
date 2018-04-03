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
package se.litsec.opensaml.common.validation;

import java.util.HashMap;
import java.util.Map;

import org.opensaml.saml.common.assertion.ValidationContext;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

/**
 * Abstract base class for building the {@link ValidationContext} object using a builder pattern. These settings are
 * parameters that control the validation of an object.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public abstract class AbstractValidationParametersBuilder<T extends AbstractValidationParametersBuilder<T>> implements
    ValidationParametersBuilder {

  /** The static parameters. */
  private Map<String, Object> staticParameters = new HashMap<>();

  /** The dynamic parameters. */
  private Map<String, Object> dynamicParameters = new HashMap<>();

  /** {@inheritDoc} */
  @Override
  public ValidationContext build() {
    ValidationContext context = new ValidationContext(this.staticParameters);
    context.getDynamicParameters().putAll(this.dynamicParameters);
    return context;
  }

  /**
   * Generic method that adds a static validation parameter.
   * 
   * @param name
   *          the parameter name
   * @param value
   *          the parameter value
   * @return the builder
   */
  public T staticParameter(String name, Object value) {
    this.addStaticParameter(name, value);
    return this.getThis();
  }

  /**
   * Generic method that adds a dynamic validation parameter.
   * 
   * @param name
   *          the parameter name
   * @param value
   *          the parameter value
   * @return the builder
   */
  public T dynamicParameter(String name, Object value) {
    this.addStaticParameter(name, value);
    return this.getThis();
  }

  /**
   * Tells whether strict validation should be performed.
   * 
   * @param flag
   *          true/false
   * @return the builder
   */
  public T strictValidation(Boolean flag) {
    this.addStaticParameter(CoreValidatorParameters.STRICT_VALIDATION, flag);
    return this.getThis();
  }

  /**
   * Gives the number of milliseconds that is the maximum allowed clock skew when verifying time stamps.
   * 
   * @param millis
   *          number of milliseconds
   * @return the builder
   */
  public T allowedClockSkew(Long millis) {
    this.addStaticParameter(CoreValidatorParameters.ALLOWED_CLOCK_SKEW, millis);
    return this.getThis();
  }

  /**
   * Gives the maximum age (difference between issuance time and the validation time) that a received message is allowed
   * to have.
   * 
   * @param millis
   *          number of milliseconds
   * @return the builder
   */
  public T maxAgeReceivedMessage(Long millis) {
    this.addStaticParameter(CoreValidatorParameters.MAX_AGE_MESSAGE, millis);
    return this.getThis();
  }

  /**
   * Tells whether we require an object being validated to be signed.
   * 
   * @param required
   *          true/false
   * @return the builder
   */
  public T signatureRequired(Boolean required) {
    this.addStaticParameter(CoreValidatorParameters.SIGNATURE_REQUIRED, required);
    return this.getThis();
  }

  /**
   * Gives the trust basis criteria set to use when verifying signatures ({@code SignatureTrustEngine.validate}).
   * 
   * @param criteriaSet
   *          the criteria set
   * @return the builder
   */
  public T signatureValidationCriteriaSet(CriteriaSet criteriaSet) {
    this.addStaticParameter(CoreValidatorParameters.SIGNATURE_VALIDATION_CRITERIA_SET, criteriaSet);
    return this.getThis();
  }

  /**
   * Returns 'this' object.
   * 
   * @return this object (the concrete builder}
   */
  protected abstract T getThis();

  /**
   * Adds a static validation parameter.
   * 
   * @param name
   *          the name of the parameter
   * @param value
   *          the value of the parameter
   */
  protected final void addStaticParameter(String name, Object value) {
    this.staticParameters.put(name, value);
  }

  /**
   * Adds a dynamic validation parameter.
   * 
   * @param name
   *          the name of the parameter
   * @param value
   *          the value of the parameter
   */
  protected final void addDynamicParameter(String name, Object value) {
    this.staticParameters.put(name, value);
  }

}
