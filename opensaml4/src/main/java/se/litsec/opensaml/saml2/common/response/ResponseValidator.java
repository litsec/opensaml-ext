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

import static se.litsec.opensaml.common.validation.ValidationSupport.check;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.SAML20AssertionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import se.litsec.opensaml.common.validation.AbstractSignableObjectValidator;
import se.litsec.opensaml.common.validation.CoreValidatorParameters;
import se.litsec.opensaml.common.validation.ValidationSupport.ValidationResultException;

/**
 * Response validator that ensures that a {@code Response} element is valid according to the 2.0 SAML Core specification
 * and makes checks based on the supplied validation context parameters described below.
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * </p>
 * <ul>
 * <li>The static parameters defined for {@link AbstractSignableObjectValidator}.</li>
 * <li>{@link CoreValidatorParameters#STRICT_VALIDATION}: Optional. If not supplied, defaults to 'false'. Tells whether
 * strict validation should be performed.
 * </li>
 * <li>{@link SAML2AssertionValidationParameters#CLOCK_SKEW}: Optional. Gives the duration that is the
 * maximum allowed clock skew. If not given {@link SAML20AssertionValidator#DEFAULT_CLOCK_SKEW} is used.
 * </li>
 * <li>{@link CoreValidatorParameters#MAX_AGE_MESSAGE}: Optional. Gives the maximum age (difference between issuance
 * time and the validation time). If not given, the {@link #DEFAULT_MAX_AGE_RECEIVED_MESSAGE} is used.
 * </li>
 * <li>{@link CoreValidatorParameters#RECEIVE_INSTANT}: Optional. Gives the timestamp (milliseconds since epoch) for
 * when the response message was received. If not given the current time is used.
 * </li>
 * <li>{@link CoreValidatorParameters#AUTHN_REQUEST}: Optional. If supplied will be used in a number of validations when
 * information from the corresponding {@code AuthnRequest} is needed. If not supplied, other, more detailed parameters
 * must be given.
 * </li>
 * <li>{@link CoreValidatorParameters#AUTHN_REQUEST_ID}: Required if the {@link CoreValidatorParameters#AUTHN_REQUEST}
 * is not assigned. Is used when validating the {@code InResponseTo} attribute of the response.
 * </li>
 * <li>{@link CoreValidatorParameters#RECEIVE_URL}: Required. A String holding the URL on which we received the response
 * message. Is used when the {@code Destination} attribute is validated.
 * </li>
 * <li>{@link CoreValidatorParameters#EXPECTED_ISSUER}: Optional. If set, is used when the issuer of the response is
 * validated.</li>
 * </ul>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ResponseValidator extends AbstractSignableObjectValidator<Response> {

  /** Class logger. */
  private final Logger log = LoggerFactory.getLogger(ResponseValidator.class);

  /**
   * Constructor.
   * 
   * @param trustEngine
   *          the trust used to validate the object's signature
   * @param signaturePrevalidator
   *          the signature pre-validator used to pre-validate the object's signature
   */
  public ResponseValidator(final SignatureTrustEngine trustEngine, final SignaturePrevalidator signaturePrevalidator) {
    super(trustEngine, signaturePrevalidator);
  }

  /** {@inheritDoc} */
  @Override
  public ValidationResult validate(final Response response, final ValidationContext context) {

    try {
      check(this.validateID(response, context));
      check(this.validateVersion(response, context));
      check(this.validateStatus(response, context));
      check(this.validateIssueInstant(response, context));
      check(this.validateInResponseTo(response, context));
      check(this.validateDestination(response, context));
      check(this.validateConsent(response, context));
      check(this.validateIssuer(response, context));
      check(this.validateSignature(response, context));
      check(this.validateAssertions(response, context));
      check(this.validateExtensions(response, context));
    }
    catch (ValidationResultException e) {
      return e.getResult();
    }

    return ValidationResult.VALID;
  }

  /**
   * Validates that the {@code Response} object has an ID attribute.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateID(final Response response, final ValidationContext context) {
    if (!StringUtils.hasText(response.getID())) {
      context.setValidationFailureMessage("Missing ID attribute in Response");
      return ValidationResult.INVALID;
    }
    return ValidationResult.VALID;
  }

  /**
   * Validates that the {@code Response} object has a valid Version attribute.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateVersion(final Response response, final ValidationContext context) {
    if (response.getVersion() == null || !response.getVersion().toString().equals(SAMLVersion.VERSION_20.toString())) {
      context.setValidationFailureMessage("Invalid SAML version in Response");
      return ValidationResult.INVALID;
    }
    return ValidationResult.VALID;
  }

  /**
   * Validates that the {@code Response} object has a {@code Status} attribute.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateStatus(final Response response, final ValidationContext context) {
    if (response.getStatus() == null
        || response.getStatus().getStatusCode() == null
        || response.getStatus().getStatusCode().getValue() == null) {
      context.setValidationFailureMessage("Missing Status/StatusCode in Response");
      return ValidationResult.INVALID;
    }
    return ValidationResult.VALID;
  }

  /**
   * Validates that the {@code Response} object has a IssueInstant attribute and that it is not too old given the
   * {@link CoreValidatorParameters#MAX_AGE_MESSAGE} and {@link CoreValidatorParameters#RECEIVE_INSTANT} context
   * parameters.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateIssueInstant(final Response response, final ValidationContext context) {
    if (response.getIssueInstant() == null) {
      context.setValidationFailureMessage("Missing IssueInstant attribute in Response");
      return ValidationResult.INVALID;
    }

    final Instant receiveInstant = getReceiveInstant(context); 
    final Instant issueInstant = response.getIssueInstant();
    
    final Duration maxAgeResponse = getMaxAgeReceivedMessage(context);
    final Duration allowedClockSkew = getAllowedClockSkew(context);

    // Too old?
    // receiveInstant - issueInstant > maxAgeResponse + allowedClockSkew
    //
    if (Duration.between(receiveInstant, issueInstant).compareTo(maxAgeResponse.plus(allowedClockSkew)) > 0) {
      final String msg = String.format("Received Response message is too old - issue-instant: %s - receive-time: %s",
        response.getIssueInstant(), receiveInstant); 
      context.setValidationFailureMessage(msg);
      return ValidationResult.INVALID;
    }

    // Not yet valid? -> Clock skew is unacceptable.
    // issueInstant - receiveInstant > allowedClockSkew
    //
    if (Duration.between(issueInstant, receiveInstant).compareTo(allowedClockSkew) > 0) {
      final String msg = String.format("Issue-instant of Response message (%s) is newer than receive time (%s) - Non accepted clock skew",
        response.getIssueInstant(), receiveInstant); 
      context.setValidationFailureMessage(msg);
      return ValidationResult.INVALID;
    }

    return ValidationResult.VALID;
  }

  /**
   * Ensures that the {@code InResponseTo} attribute is present and that it matches the ID of the {@code AuthnRequest}.
   * The ID is found in the {@code context} parameter under the key {@link CoreValidatorParameters#AUTHN_REQUEST_ID} or
   * from the object stored under {@link CoreValidatorParameters#AUTHN_REQUEST}.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateInResponseTo(final Response response, final ValidationContext context) {
    if (response.getInResponseTo() == null) {
      context.setValidationFailureMessage("Missing InResponseTo attribute in Response");
      return ValidationResult.INVALID;
    }
    String expectedInResponseTo = (String) context.getStaticParameters().get(CoreValidatorParameters.AUTHN_REQUEST_ID);
    if (expectedInResponseTo == null) {
      AuthnRequest authnRequest = (AuthnRequest) context.getStaticParameters().get(CoreValidatorParameters.AUTHN_REQUEST);
      if (authnRequest != null) {
        expectedInResponseTo = authnRequest.getID();
      }
    }
    if (expectedInResponseTo != null) {
      if (!response.getInResponseTo().equals(expectedInResponseTo)) {
        final String msg = String.format("Expected Response message for AuthnRequest with ID '%s', but this Response is for '%s'",
          expectedInResponseTo, response.getInResponseTo());
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
      }
    }
    else {
      context.setValidationFailureMessage("Could not validate InResponseTo of Response (no AuthnRequest ID available)");
      return ValidationResult.INDETERMINATE;
    }

    return ValidationResult.VALID;
  }

  /**
   * Ensures that the {@code Destination} attribute is present and matches the URL on which we received the message.
   * This value is found in the context under the {@link CoreValidatorParameters#RECEIVE_URL} key.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateDestination(final Response response, final ValidationContext context) {
    if (response.getDestination() == null) {
      context.setValidationFailureMessage("Missing Destination attribute in Response");
      return ValidationResult.INVALID;
    }
    final String receiveUrl = (String) context.getStaticParameters().get(CoreValidatorParameters.RECEIVE_URL);
    if (receiveUrl != null) {
      if (!response.getDestination().equals(receiveUrl)) {
        final String msg = String.format("Destination attribute (%s) of Response does not match URL on which response was received (%s)",
          response.getDestination(), receiveUrl);
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
      }
    }
    else {
      context.setValidationFailureMessage("Could not validate Destination of Response (no receive URL available)");
      return ValidationResult.INDETERMINATE;
    }

    return ValidationResult.VALID;
  }

  /**
   * Validates the {@code Consent} attribute. The default implementation returns {@link ValidationResult#VALID} since
   * the attribute is optional according to the SAML 2.0 Core specifications.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateConsent(final Response response, final ValidationContext context) {
    return ValidationResult.VALID;
  }

  /**
   * Ensures that the {@code Issuer} element is present and matches the expected issuer (if set in the context under the
   * {@link CoreValidatorParameters#EXPECTED_ISSUER} key).
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateIssuer(final Response response, final ValidationContext context) {
    final String issuer = this.getIssuer(response);
    if (issuer == null) {
      context.setValidationFailureMessage("Missing Issuer element in Response");
      return ValidationResult.INVALID;
    }
    final String expectedIssuer = (String) context.getStaticParameters().get(CoreValidatorParameters.EXPECTED_ISSUER);
    if (expectedIssuer != null) {
      if (!response.getIssuer().getValue().equals(expectedIssuer)) {
        final String msg = String.format("Issuer of Response (%s) did not match expected issuer (%s)",
          response.getIssuer().getValue(), expectedIssuer);
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
      }
    }
    else {
      log.warn("EXPECTED_ISSUER key not set - will not check issuer of Response");
    }

    return ValidationResult.VALID;
  }

  /**
   * Validates the {@code Assertion} and/or {@code EncryptedAssertion} element. The default implementation checks:
   * <ul>
   * <li>If status is success - At least of assertion (or encrypted assertion) is present.</li>
   * <li>Else - No assertions are present.</li>
   * </ul>
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateAssertions(final Response response, final ValidationContext context) {
    if (StatusCode.SUCCESS.equals(response.getStatus().getStatusCode().getValue())) {
      if (response.getAssertions().isEmpty() && response.getEncryptedAssertions().isEmpty()) {
        context.setValidationFailureMessage("Response message has success status but does not contain any assertions - invalid");
        return ValidationResult.INVALID;
      }
    }
    else {
      if (response.getAssertions().size() > 0 || response.getEncryptedAssertions().size() > 0) {
        context.setValidationFailureMessage("Response message has failure status but contains assertions - invalid");
        return ValidationResult.INVALID;
      }
    }
    return ValidationResult.VALID;
  }

  /**
   * Validates the {@code Extensions} element. The default implementation returns {@link ValidationResult#VALID} since
   * the element is optional according to the SAML 2.0 Core specifications.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateExtensions(final Response response, final ValidationContext context) {
    return ValidationResult.VALID;
  }

  /** {@inheritDoc} */
  @Override
  protected String getIssuer(final Response signableObject) {
    return Optional.ofNullable(signableObject.getIssuer()).map(Issuer::getValue).orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  protected String getID(final Response signableObject) {
    return signableObject.getID();
  }

  /** {@inheritDoc} */
  @Override
  protected String getObjectName() {
    return "Response";
  }

}
