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
package se.litsec.opensaml.saml2.common.response;

import java.time.Duration;
import java.util.Optional;

/**
 * Configuration settings for response and assertion validation.
 *
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ResponseValidationSettings {

  /** The default allowed clock skew (in milliseconds) - 30 seconds. */
  public static final Duration DEFAULT_ALLOWED_CLOCK_SKEW = Duration.ofSeconds(30); 

  /** The default age for a response message that we allow (in milliseconds) - 3 minutes. */
  public static final Duration DEFAULT_MAX_AGE_RESPONSE = Duration.ofMinutes(3); 

  /** Default max session age (in milliseconds) - 1 hour. */
  public static final Duration DEFAULT_MAX_SESSION_AGE = Duration.ofHours(1); 

  /** The allowed clock skew (in milliseconds). */
  private Duration allowedClockSkew;

  /** Maximum allowed "age" of a response message (in milliseconds). */
  private Duration maxAgeResponse;

  /** Maximum session age allowed for SSO (in milliseconds). */
  private Duration maxSessionAge;

  /** Should validation be strict? Default is false. */
  private boolean strictValidation = false;

  /** Is signed assertions required? */
  private boolean requireSignedAssertions = false;

  /**
   * Returns the allowed clock skew.
   * <p>
   * The default is {@link #DEFAULT_ALLOWED_CLOCK_SKEW}.
   * </p>
   *
   * @return the allowed clock skew
   */
  public Duration getAllowedClockSkew() {
    return Optional.ofNullable(this.allowedClockSkew).orElse(DEFAULT_ALLOWED_CLOCK_SKEW);
  }

  /**
   * Assigns the allowed clock skew.
   * <p>
   * The default is {@link #DEFAULT_ALLOWED_CLOCK_SKEW}.
   * </p>
   * 
   * @param allowedClockSkew
   *          the allowed clock skew
   */
  public void setAllowedClockSkew(final Duration allowedClockSkew) {
    this.allowedClockSkew = allowedClockSkew;
  }

  /**
   * Returns the maximum allowed "age" of a response message.
   * <p>
   * The default is {@link #DEFAULT_MAX_AGE_RESPONSE}.
   * </p>
   * 
   * @return the maximum allowed "age" of a response message
   */
  public Duration getMaxAgeResponse() {
    return Optional.ofNullable(this.maxAgeResponse).orElse(DEFAULT_MAX_AGE_RESPONSE);
  }

  /**
   * Assigns the maximum allowed "age" of a response message
   * <p>
   * The default is {@link #DEFAULT_MAX_AGE_RESPONSE}.
   * </p>
   * 
   * @param maxAgeResponse
   *          the maximum allowed "age" of a response message
   */
  public void setMaxAgeResponse(final Duration maxAgeResponse) {
    this.maxAgeResponse = maxAgeResponse;
  }

  /**
   * Returns the maximum session age allowed for SSO.
   * <p>
   * The default is {@link #DEFAULT_MAX_SESSION_AGE}.
   * </p>
   * 
   * @return the maximum session age allowed for SSO
   */
  public Duration getMaxSessionAge() {
    return Optional.ofNullable(this.maxSessionAge).orElse(DEFAULT_MAX_SESSION_AGE);
  }

  /**
   * Assigns the maximum session age allowed for SSO.
   * <p>
   * The default is {@link #DEFAULT_MAX_SESSION_AGE}.
   * </p>
   * 
   * @param maxSessionAge
   *          the maximum session age allowed for SSO
   */
  public void setMaxSessionAge(final Duration maxSessionAge) {
    this.maxSessionAge = maxSessionAge;
  }

  /**
   * Predicate telling whether strict validation should be performed.
   * <p>
   * The default is {@code false}.
   * </p>
   * 
   * @return whether strict validation should be performed
   */
  public boolean isStrictValidation() {
    return this.strictValidation;
  }

  /**
   * Assigns telling whether strict validation should be performed.
   * <p>
   * The default is {@code false}.
   * </p>
   * 
   * @param strictValidation
   *          whether strict validation should be performed
   */
  public void setStrictValidation(final boolean strictValidation) {
    this.strictValidation = strictValidation;
  }

  /**
   * Do we require signed assertions?
   * <p>
   * Should correspond the {@code WantAssertionsSigned} of the SP metadata.
   * </p>
   * 
   * @return do we require signed assertions?
   */
  public boolean isRequireSignedAssertions() {
    return this.requireSignedAssertions;
  }

  /**
   * Assigns whether we require signed assertions.
   * <p>
   * Should correspond the {@code WantAssertionsSigned} of the SP metadata.
   * </p>
   * 
   * @param requireSignedAssertions
   *          whether we require signed assertions?
   */
  public void setRequireSignedAssertions(final boolean requireSignedAssertions) {
    this.requireSignedAssertions = requireSignedAssertions;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return String.format("allowedClockSkew=%s, maxAgeResponse=%s, maxSessionAge=%s, strictValidation=%s, requireSignedAssertions=%s",
      this.allowedClockSkew, this.maxAgeResponse, this.maxSessionAge, this.strictValidation, this.requireSignedAssertions);
  }

}
