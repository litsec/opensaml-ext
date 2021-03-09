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
package se.litsec.opensaml.common.validation;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;

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
    final ValidationContext context = new ValidationContext(this.staticParameters);
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
  public T staticParameter(final String name, final Object value) {
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
  public T dynamicParameter(final String name, final Object value) {
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
  public T strictValidation(final boolean flag) {
    this.addStaticParameter(CoreValidatorParameters.STRICT_VALIDATION, flag);
    return this.getThis();
  }

  /**
   * Gives the duration that is the maximum allowed clock skew when verifying time stamps.
   * 
   * @param skew
   *          duration
   * @return the builder
   */
  public T allowedClockSkew(final Duration skew) {
    this.addStaticParameter(SAML2AssertionValidationParameters.CLOCK_SKEW, skew);
    return this.getThis();
  }

  /**
   * Gives the number of milliseconds that is the maximum allowed clock skew when verifying time stamps.
   * 
   * @param millis
   *          number of milliseconds
   * @return the builder
   */
  public T allowedClockSkew(final long millis) {
    return this.allowedClockSkew(Duration.ofMillis(millis));
  }

  /**
   * Gives the maximum age (difference between issuance time and the validation time) that a received message is allowed
   * to have.
   * 
   * @param maxAge
   *          maximum allowed age on messages
   * @return the builder
   */
  public T maxAgeReceivedMessage(final Duration maxAge) {
    this.addStaticParameter(CoreValidatorParameters.MAX_AGE_MESSAGE, maxAge);
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
  public T maxAgeReceivedMessage(final long millis) {
    return this.maxAgeReceivedMessage(Duration.ofMillis(millis));
  }

  /**
   * Sets the receive instant (i.e., when a message being validated was received).
   * 
   * @param instant
   *          the receive instant
   * @return the builder
   */
  public T receiveInstant(final Instant instant) {
    this.addStaticParameter(CoreValidatorParameters.RECEIVE_INSTANT, instant);
    return this.getThis();
  }

  /**
   * Sets the receive instant (i.e., when a message being validated was received).
   * 
   * @param instant
   *          the receive instant
   * @return the builder
   */
  public T receiveInstant(final long instant) {
    return this.receiveInstant(Instant.ofEpochMilli(instant));
  }

  /**
   * Tells whether we require an object being validated to be signed.
   * 
   * @param required
   *          true/false
   * @return the builder
   */
  public T signatureRequired(final boolean required) {
    this.addStaticParameter(SAML2AssertionValidationParameters.SIGNATURE_REQUIRED, required);
    return this.getThis();
  }

  /**
   * Gives the trust basis criteria set to use when verifying signatures ({@code SignatureTrustEngine.validate}).
   * 
   * @param criteriaSet
   *          the criteria set
   * @return the builder
   */
  public T signatureValidationCriteriaSet(final CriteriaSet criteriaSet) {
    if (criteriaSet != null) {
      this.addStaticParameter(SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_CRITERIA_SET, criteriaSet);
    }
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
  public final void addStaticParameter(final String name, final Object value) {
    if (name != null) {
      this.staticParameters.put(name, value);
    }
  }

  /**
   * Adds a static validation parameter if it is not set yet.
   * 
   * @param name
   *          the name of the parameter
   * @param value
   *          the value of the parameter
   */
  public final void addStaticParameterIfMissing(final String name, final Object value) {
    if (name != null && !this.staticParameters.containsKey(name)) {
      this.addStaticParameter(name, value);
    }
  }

  /**
   * Adds static validation parameters.
   * 
   * @param pars
   *          static validation parameters
   */
  public final void addStaticParameters(final Map<String, Object> pars) {
    if (pars != null) {
      this.staticParameters.putAll(pars);
    }
  }

  /**
   * Adds a dynamic validation parameter.
   * 
   * @param name
   *          the name of the parameter
   * @param value
   *          the value of the parameter
   */
  public final void addDynamicParameter(final String name, final Object value) {
    if (name != null) {
      this.staticParameters.put(name, value);
    }
  }

  /**
   * Adds dynamic validation parameters.
   * 
   * @param pars
   *          dynamic validation parameters
   */
  public final void addDynamicParameters(final Map<String, Object> pars) {
    if (pars != null) {
      this.dynamicParameters.putAll(pars);
    }
  }

}
