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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.common.assertion.ValidationContext;

/**
 * Abstract base class for {@link ObjectValidator}.
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * <ul>
 * <li>{@link CoreValidatorParameters#STRICT_VALIDATION}: Optional. If not supplied, defaults to 'false'. Tells whether
 * strict validation should be performed.</li>
 * <li>{@link CoreValidatorParameters#ALLOWED_CLOCK_SKEW}: Optional. Gives the number of milliseconds that is the
 * maximum allowed clock skew. If not given {@link #DEFAULT_ALLOWED_CLOCK_SKEW} is used.</li>
 * <li>{@link CoreValidatorParameters#MAX_AGE_MESSAGE}: Optional. Gives the maximum age (difference between issuance
 * time and the validation time). If not given, the {@link #DEFAULT_MAX_AGE_RECEIVED_MESSAGE} is used.</li>
 * </ul>
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public abstract class AbstractObjectValidator<T extends XMLObject> implements ObjectValidator<T> {

  /** The default allowed clock skew - {@value} milliseconds. */
  public static final long DEFAULT_ALLOWED_CLOCK_SKEW = 30000L;

  /** The default value for how old a received message is allowed to be - {@value} milliseconds. */
  public static final long DEFAULT_MAX_AGE_RECEIVED_MESSAGE = 180000;

  /**
   * Tells whether this validator runs in "strict" mode. This value is read from the static validation context parameter
   * {@link CoreValidatorParameters#STRICT_VALIDATION}. If this parameter is not available {@code false} is returned.
   * 
   * @param context
   *          the validation context
   * @return {@code true} for strict mode, and {@code false} otherwise
   */
  public static boolean isStrictValidation(ValidationContext context) {
    Boolean strict = (Boolean) context.getStaticParameters().get(CoreValidatorParameters.STRICT_VALIDATION);
    return strict != null ? strict.booleanValue() : false;
  }

  /**
   * Returns the number of milliseconds that is the maximum allowed clock skew that we accept when comparing time
   * stamps. The value is read from the static validation context parameter
   * {@link CoreValidatorParameters#ALLOWED_CLOCK_SKEW}. If this parameter is not available,
   * {@link #DEFAULT_ALLOWED_CLOCK_SKEW} is used.
   * 
   * @param context
   *          the validation context
   * @return the number of milliseconds that is the maximum allowed clock skew
   */
  public static long getAllowedClockSkew(ValidationContext context) {
    Long clockSkew = (Long) context.getStaticParameters().get(CoreValidatorParameters.ALLOWED_CLOCK_SKEW);
    return clockSkew != null ? clockSkew.longValue() : DEFAULT_ALLOWED_CLOCK_SKEW;
  }

  /**
   * Returns the number of milliseconds that a received message (or element) is allowed to less than the current time.
   * The value is read from the static validation context parameter {@link CoreValidatorParameters#MAX_AGE_MESSAGE}. If
   * this parameter is not available, {@link #DEFAULT_MAX_AGE_RECEIVED_MESSAGE} is used.
   * 
   * @param context
   *          the validation context
   * @return the maximum number of milliseconds that may have elapsed since the issuance of a message and validation of
   *         it
   */
  public static long getMaxAgeReceivedMessage(ValidationContext context) {
    Long maxAge = (Long) context.getStaticParameters().get(CoreValidatorParameters.MAX_AGE_MESSAGE);
    return maxAge != null ? maxAge.longValue() : DEFAULT_MAX_AGE_RECEIVED_MESSAGE;
  }

  /**
   * Returns the timestamp (milliseconds since epoch) for when the message being validated was received. The value is
   * read from {@link CoreValidatorParameters#RECEIVE_INSTANT}. If the parameter is not available the current time is
   * returned.
   * 
   * @param context
   *          the validation context.
   * @return the timestamp for when the message being validated was received
   */
  public static long getReceiveInstant(ValidationContext context) {
    Long receiveInstant = (Long) context.getStaticParameters().get(CoreValidatorParameters.RECEIVE_INSTANT);
    return receiveInstant != null ? receiveInstant.longValue() : System.currentTimeMillis();
  }

}
