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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;

import net.shibboleth.utilities.java.support.primitive.DeprecationSupport;
import net.shibboleth.utilities.java.support.primitive.DeprecationSupport.ObjectType;

/**
 * Abstract base class for {@link ObjectValidator}.
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * </p>
 * <ul>
 * <li>{@link CoreValidatorParameters#STRICT_VALIDATION}: Optional. If not supplied, defaults to 'false'. Tells whether
 * strict validation should be performed.</li>
 * <li>{@link SAML2AssertionValidationParameters#CLOCK_SKEW}: Optional. Gives the duration that is the maximum allowed
 * clock skew. If not given {@link SAML20AssertionValidator#DEFAULT_CLOCK_SKEW} is used.</li>
 * <li>{@link CoreValidatorParameters#MAX_AGE_MESSAGE}: Optional. Duration that gives the maximum age (difference
 * between issuance time and the validation time). If not given, the {@link #DEFAULT_MAX_AGE_RECEIVED_MESSAGE} is
 * used.</li>
 * <li>{@link CoreValidatorParameters#RECEIVE_INSTANT}: Optional. Instant giving the time when the message was received.
 * If not set, the current time is used.</li>
 * </ul>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public abstract class AbstractObjectValidator<T extends XMLObject> implements ObjectValidator<T> {

  /** The default value for how old a received message is allowed to be. */
  public static final Duration DEFAULT_MAX_AGE_RECEIVED_MESSAGE = Duration.ofMinutes(3);

  /**
   * Tells whether this validator runs in "strict" mode. This value is read from the static validation context parameter
   * {@link CoreValidatorParameters#STRICT_VALIDATION}. If this parameter is not available {@code false} is returned.
   * 
   * @param context
   *          the validation context
   * @return {@code true} for strict mode, and {@code false} otherwise
   */
  public static boolean isStrictValidation(final ValidationContext context) {
    final Boolean strict = (Boolean) context.getStaticParameters().get(CoreValidatorParameters.STRICT_VALIDATION);
    return strict != null ? strict.booleanValue() : false;
  }

  /**
   * Returns the duration that is the maximum allowed clock skew that we accept when comparing time stamps. The value is
   * read from the static validation context parameter {@link SAML2AssertionValidationParameters#CLOCK_SKEW}. If this
   * parameter is not available {@link SAML20AssertionValidator#DEFAULT_CLOCK_SKEW} is used.
   * 
   * @param context
   *          the validation context
   * @return the duration that is the maximum allowed clock skew
   */
  public static Duration getAllowedClockSkew(final ValidationContext context) {
    final Object object = context.getStaticParameters().get(SAML2AssertionValidationParameters.CLOCK_SKEW);
    if (object != null) {
      if (Duration.class.isInstance(object)) {
        return Duration.class.cast(object);
      }
      else if (Long.class.isInstance(object)) {
        DeprecationSupport.warn(ObjectType.CONFIGURATION, SAML2AssertionValidationParameters.CLOCK_SKEW, null, Duration.class.getName());        
        return Duration.ofMillis(Long.class.cast(object));
      }
    }
    return SAML20AssertionValidator.DEFAULT_CLOCK_SKEW;
  }

  /**
   * Returns the duration that a received message (or element) is allowed to less than the current time. The value is
   * read from the static validation context parameter {@link CoreValidatorParameters#MAX_AGE_MESSAGE}. If this
   * parameter is not available, {@link #DEFAULT_MAX_AGE_RECEIVED_MESSAGE} is used.
   * 
   * @param context
   *          the validation context
   * @return the duration that may have elapsed since the issuance of a message and validation of it
   */
  public static Duration getMaxAgeReceivedMessage(final ValidationContext context) {
    final Object object = context.getStaticParameters().get(CoreValidatorParameters.MAX_AGE_MESSAGE);
    if (object != null) {
      if (Duration.class.isInstance(object)) {
        return Duration.class.cast(object);
      }
      else if (Long.class.isInstance(object)) {
        DeprecationSupport.warn(ObjectType.CONFIGURATION, CoreValidatorParameters.MAX_AGE_MESSAGE, null, Duration.class.getName());
        return Duration.ofMillis(Long.class.cast(object));
      }
    }
    return DEFAULT_MAX_AGE_RECEIVED_MESSAGE;
  }

  /**
   * Returns the timestamp for when the message being validated was received. The value is
   * read from {@link CoreValidatorParameters#RECEIVE_INSTANT}. If the parameter is not available the current time is
   * returned.
   * 
   * @param context
   *          the validation context.
   * @return the timestamp for when the message being validated was received
   */
  public static Instant getReceiveInstant(final ValidationContext context) {
    final Object object = context.getStaticParameters().get(CoreValidatorParameters.RECEIVE_INSTANT);
    if (object != null) {
      if (Instant.class.isInstance(object)) {
        return Instant.class.cast(object);
      }
      else if (Long.class.isInstance(object)) {
        DeprecationSupport.warn(ObjectType.CONFIGURATION, CoreValidatorParameters.RECEIVE_INSTANT, null, Duration.class.getName());
        return Instant.ofEpochMilli(Long.class.cast(object));
      }
    }
    return Instant.now(); 
  }

}
