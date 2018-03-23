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

import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.AuthnRequest;

/**
 * Core parameter keys used to store and retrieve static and dynamic parameters within a
 * {@link org.opensaml.saml.common.assertion.ValidationContext}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class CoreValidatorParameters {

  /** The standard prefix for all SAML 2 parameters defined in this set. */
  public static final String STD_PREFIX = SAML2AssertionValidationParameters.STD_PREFIX;

  /**
   * Carries a {@link java.lang.Long} specifying a clock skew value in milliseconds.
   */
  public static final String ALLOWED_CLOCK_SKEW = SAML2AssertionValidationParameters.CLOCK_SKEW;
  
  /**
   * Carries a {@link Boolean} specifying whether the validation is strict or not.
   */
  public static final String STRICT_VALIDATION = STD_PREFIX + ".StrictValidation";

  /**
   * Carries a {@link java.lang.Boolean} flag which indicates whether an element being validated is required to be
   * signed.
   */
  public static final String SIGNATURE_REQUIRED = SAML2AssertionValidationParameters.SIGNATURE_REQUIRED; 

  /**
   * Carries a {@link net.shibboleth.utilities.java.support.resolver.CriteriaSet} which will be used as the input to a
   * {@link org.opensaml.xmlsec.signature.support.SignatureTrustEngine}.
   */
  public static final String SIGNATURE_VALIDATION_CRITERIA_SET = SAML2AssertionValidationParameters.SIGNATURE_VALIDATION_CRITERIA_SET;
  
  /**
   * Carries a {@link String} that holds the entityID of the expected issuer of a element.
   */
  public static final String EXPECTED_ISSUER = STD_PREFIX + ".ExpectedIssuer";
  
  /**
   * Carries a {@link Long} holding the number of milliseconds that is the max age of a received message.
   */
  public static final String MAX_AGE_MESSAGE = STD_PREFIX + ".MaxAgeReceivedMessage";
  
  /**
   * Carries a {@link Long} holding the timestamp for when a message being validated was received.
   */
  public static final String RECEIVE_INSTANT = STD_PREFIX + ".ReceiveInstant";
  
  /**
   * Carries a {@link String} that holds the URL on which a message was received.
   */
  public static final String RECEIVE_URL = STD_PREFIX + ".ReceiveURL";  
  
  /**
   * Carries a {@link AuthnRequest} object that is used in several checks of responses and assertions. The
   * {@code AuthnRequest} object is the message that was sent in order to obtain the response/assertion.
   */
  public static final String AUTHN_REQUEST = STD_PREFIX + ".AuthnRequest";
  
  /**
   * Carries a {@link String} that holds the {@code AuthnRequest} ID attribute.
   */
  public static final String AUTHN_REQUEST_ID = STD_PREFIX + ".AuthnRequestID";  
  
  // Hidden
  private CoreValidatorParameters() {
  }

}
