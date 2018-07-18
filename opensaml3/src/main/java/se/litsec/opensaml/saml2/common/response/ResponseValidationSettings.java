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
package se.litsec.opensaml.saml2.common.response;

import net.shibboleth.utilities.java.support.annotation.Duration;

/**
 * Configuration settings for response and assertion validation.
 *
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ResponseValidationSettings {

  /** The default allowed clock skew (in milliseconds) - 30 seconds. */
  public static final long DEFAULT_ALLOWED_CLOCK_SKEW = 30000L;

  /** The default age for a response message that we allow (in milliseconds) - 3 minutes. */
  public static final long DEFAULT_MAX_AGE_RESPONSE = 180000L;

  /** Default max session age (in milliseconds) - 1 hour. */
  public static final long DEFAULT_MAX_SESSION_AGE = 3600000L;

  /** The allowed clock skew (in milliseconds). */
  @Duration
  private long allowedClockSkew = DEFAULT_ALLOWED_CLOCK_SKEW;

  /** Maximum allowed "age" of a response message (in milliseconds). */
  @Duration
  private long maxAgeResponse;

  /** Maximum session age allowed for SSO (in milliseconds). */
  @Duration
  private long maxSessionAge;

  /** Should validation be strict? Default is false. */
  private boolean strictValidation = false;

  /** Is signed assertions required? */
  private boolean requireSignedAssertions = false;

  /**
   * Returns the allowed clock skew (in milliseconds).
   * <p>
   * The default is {@link #DEFAULT_ALLOWED_CLOCK_SKEW}.
   * </p>
   *
   * @return the allowed clock skew (in milliseconds)
   */
  public long getAllowedClockSkew() {
    return this.allowedClockSkew;
  }

  /**
   * Assigns the allowed clock skew (in milliseconds).
   * <p>
   * The default is {@link #DEFAULT_ALLOWED_CLOCK_SKEW}.
   * </p>
   * 
   * @param allowedClockSkew
   *          the allowed clock skew (in milliseconds)
   */
  public void setAllowedClockSkew(long allowedClockSkew) {
    this.allowedClockSkew = allowedClockSkew;
  }

  /**
   * Returns the maximum allowed "age" of a response message (in milliseconds).
   * <p>
   * The default is {@link #DEFAULT_MAX_AGE_RESPONSE}.
   * </p>
   * 
   * @return the maximum allowed "age" of a response message (in milliseconds)
   */
  public long getMaxAgeResponse() {
    return this.maxAgeResponse;
  }

  /**
   * Assigns the maximum allowed "age" of a response message (in milliseconds).
   * <p>
   * The default is {@link #DEFAULT_MAX_AGE_RESPONSE}.
   * </p>
   * 
   * @param maxAgeResponse
   *          the maximum allowed "age" of a response message (in milliseconds)
   */
  public void setMaxAgeResponse(long maxAgeResponse) {
    this.maxAgeResponse = maxAgeResponse;
  }

  /**
   * Returns the maximum session age allowed for SSO (in milliseconds).
   * <p>
   * The default is {@link #DEFAULT_MAX_SESSION_AGE}.
   * </p>
   * 
   * @return the maximum session age allowed for SSO (in milliseconds)
   */
  public long getMaxSessionAge() {
    return this.maxSessionAge;
  }

  /**
   * Assigns the maximum session age allowed for SSO (in milliseconds).
   * <p>
   * The default is {@link #DEFAULT_MAX_SESSION_AGE}.
   * </p>
   * 
   * @param maxSessionAge
   *          the maximum session age allowed for SSO (in milliseconds)
   */
  public void setMaxSessionAge(long maxSessionAge) {
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
  public void setStrictValidation(boolean strictValidation) {
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
  public void setRequireSignedAssertions(boolean requireSignedAssertions) {
    this.requireSignedAssertions = requireSignedAssertions;
  }

  @Override
  public String toString() {
    return String.format("allowedClockSkew=%s, maxAgeResponse=%s, maxSessionAge=%s, strictValidation=%s, requireSignedAssertions=%s",
      this.allowedClockSkew, this.maxAgeResponse, this.maxSessionAge, this.strictValidation, this.requireSignedAssertions);
  }

}
