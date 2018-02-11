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

import static se.litsec.opensaml.common.validation.ValidationSupport.check;

import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import se.litsec.opensaml.common.validation.AbstractObjectValidator;
import se.litsec.opensaml.common.validation.ValidationSupport.ValidationResultException;

/**
 * Response validator that ensures that a {@code Response} element is valid according to the 2.0 SAML Core
 * specification.
 * <p>
 * In order to make the validator more specific for a certain profile, inherit from the class and override the necessary
 * checks.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ResponseProfileValidator extends AbstractObjectValidator<Response> {

  /** Logging instance. */
  private final Logger log = LoggerFactory.getLogger(ResponseProfileValidator.class);

  /** {@inheritDoc} */
  @Override
  public ValidationResult validate(Response response, ValidationContext context) {

    try {
      check(this.validateRequired(response, context));
      check(this.validateInResponseTo(response, context));
      check(this.validateDestination(response, context));
      check(this.validateConsent(response, context));
      check(this.validateIssuer(response, context));
      check(this.validateSignaturePresent(response, context));
      check(this.validateAssertions(response, context));
    }
    catch (ValidationResultException e) {
      return e.getResult();
    }

    if (StatusCode.SUCCESS.equals(response.getStatus().getStatusCode().getValue())) {
      if (!response.getAssertions().isEmpty()) {
        // context.setValidationFailureMessage("Response element contains non encrypted Assertion(s) - this is not
        // valid");
        // return ValidationResult.INVALID;
        log.warn("Response element contains non encrypted Assertion(s) - this is not valid");
      }
      if (response.getEncryptedAssertions().isEmpty()) {
        // context.setValidationFailureMessage("Response element does not contain an EncryptedAssertion");
        // return ValidationResult.INVALID;
        log.warn("Response element does not contain an EncryptedAssertion");
      }
      if (response.getEncryptedAssertions().size() > 1) {
        String err = "Response element contains more than one EncryptedAssertion - this is not valid";
        if (this.isStrictValidation()) {
          context.setValidationFailureMessage(err);
          return ValidationResult.INVALID;
        }
        else {
          log.warn(err);
        }
      }
    }

    return ValidationResult.VALID;
  }

  /**
   * Validates that all elements and attributes that are required according to the SAML Core specification are present.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  public ValidationResult validateRequired(Response response, ValidationContext context) {

    if (!StringUtils.hasText(response.getID())) {
      context.setValidationFailureMessage("Missing ID attribute in Response");
      return ValidationResult.INVALID;
    }
    if (response.getStatus() == null || response.getStatus().getStatusCode() == null || response.getStatus()
      .getStatusCode()
      .getValue() == null) {
      context.setValidationFailureMessage("Missing Status/StatusCode in Response");
      return ValidationResult.INVALID;
    }
    if (response.getVersion() == null || !response.getVersion().toString().equals(org.opensaml.saml.common.SAMLVersion.VERSION_20
      .toString())) {
      context.setValidationFailureMessage("Invalid SAML version in Response");
      return ValidationResult.INVALID;
    }
    if (response.getIssueInstant() == null) {
      context.setValidationFailureMessage("Missing IssueInstant attribute in Response");
      return ValidationResult.INVALID;
    }

    return ValidationResult.VALID;
  }

  /**
   * Validates the {@code InResponseTo} attribute. The default implementation returns {@link ValidationResult#VALID}
   * since the attribute is optional according to the SAML 2.0 Core specifications.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  public ValidationResult validateInResponseTo(Response response, ValidationContext context) {
    return ValidationResult.VALID;
  }

  /**
   * Validates the {@code Destination} attribute. The default implementation returns {@link ValidationResult#VALID}
   * since the attribute is optional according to the SAML 2.0 Core specifications.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  public ValidationResult validateDestination(Response response, ValidationContext context) {
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
  public ValidationResult validateConsent(Response response, ValidationContext context) {
    return ValidationResult.VALID;
  }

  /**
   * Validates the {@code Issuer} element. The default implementation returns {@link ValidationResult#VALID} since the
   * element is optional according to the SAML 2.0 Core specifications.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  public ValidationResult validateIssuer(Response response, ValidationContext context) {
    return ValidationResult.VALID;
  }

  /**
   * Validates that the {@code Signature} element is present (if required). The default implementation always returns
   * {@link ValidationResult#VALID} since the element is optional according to the SAML 2.0 Core specifications.
   * 
   * @param response
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  public ValidationResult validateSignaturePresent(Response response, ValidationContext context) {
    return ValidationResult.VALID;
  }

  /**
   * Validates the {@code Assertion} and/or {@code EncryptedAssertion} element. The default implementation 
   * checks:
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
  public ValidationResult validateAssertions(Response response, ValidationContext context) {
    if (StatusCode.SUCCESS.equals(response.getStatus().getStatusCode().getValue())) {
      if (response.getAssertions().isEmpty() && response.getEncryptedAssertions().isEmpty()) {
        context.setValidationFailureMessage("Response message has success status but does not contain assertions - invalid");
      }
    }
    else {
      if (response.getAssertions().size() > 0 || response.getEncryptedAssertions().size() > 0) {
        context.setValidationFailureMessage("Response message has failure status but contains assertions - invalid");
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
  public ValidationResult validateExtensions(Response response, ValidationContext context) {
    return ValidationResult.VALID;
  }

}
