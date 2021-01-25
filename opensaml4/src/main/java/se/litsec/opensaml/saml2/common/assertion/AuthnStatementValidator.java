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
package se.litsec.opensaml.saml2.common.assertion;

import static se.litsec.opensaml.common.validation.ValidationSupport.check;

import java.time.Instant;

import javax.xml.namespace.QName;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.StatementValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.AuthnStatement;
import org.opensaml.saml.saml2.core.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.litsec.opensaml.common.validation.AbstractObjectValidator;
import se.litsec.opensaml.common.validation.CoreValidatorParameters;
import se.litsec.opensaml.common.validation.ValidationSupport.ValidationResultException;

/**
 * Core statement validator for {@link AuthnStatement}s.
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * </p>
 * <ul>
 * <li>{@link CoreValidatorParameters#AUTHN_REQUEST}: Optional. If supplied will be used in a number of validations when
 * information from the corresponding {@code AuthnRequest} is needed. If not supplied, other, more detailed parameters
 * must be given.</li>
 * <li>{@link #AUTHN_REQUEST_FORCE_AUTHN}: If the above {@link CoreValidatorParameters#AUTHN_REQUEST} is not assigned,
 * this parameter gives the {@code ForceAuthn} flag. This is used to determine if a valid assertion was issued based on
 * SSO/non-SSO.</li>
 * <li>{@link #AUTHN_REQUEST_ISSUE_INSTANT}: If the above {@link CoreValidatorParameters#AUTHN_REQUEST} is not assigned,
 * this parameter gives the issue instant of the authentication request. This is used to determine if a valid assertion
 * was issued based on SSO/non-SSO.</li>
 * <li>{@link #MAX_ACCEPTED_SSO_SESSION_TIME}: For SSO, we may want to assert that the authentication is not too old. If
 * so, this parameter gives the maximum accepted session time.</li>
 * </ul>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AuthnStatementValidator implements StatementValidator {

  /**
   * Key for a validation context parameter. Carries a {@link Boolean} holding the value of the ForceAuthn flag from the
   * AuthnRequest.
   */
  public static final String AUTHN_REQUEST_FORCE_AUTHN = CoreValidatorParameters.STD_PREFIX + ".AuthnRequestForceAuthn";

  /**
   * Key for a validation context parameter. Carries a {@link Long} holding the issuance time for the AuthnRequest.
   */
  public static final String AUTHN_REQUEST_ISSUE_INSTANT = CoreValidatorParameters.STD_PREFIX + ".AuthnRequestIssueInstant";

  /**
   * Key for a validation context parameter. Carries a {@link Long} holding the maximum session time that we can accept
   * for SSO.
   */
  public static final String MAX_ACCEPTED_SSO_SESSION_TIME = CoreValidatorParameters.STD_PREFIX + ".MaxAcceptedSsoSessionTime";

  /** Class logger. */
  private final Logger log = LoggerFactory.getLogger(AuthnStatementValidator.class);

  /** {@inheritDoc} */
  @Override
  public QName getServicedStatement() {
    return AuthnStatement.DEFAULT_ELEMENT_NAME;
  }

  /** {@inheritDoc} */
  @Override
  public final ValidationResult validate(final Statement statement, final Assertion assertion, final ValidationContext context)
      throws AssertionValidationException {

    if (statement instanceof AuthnStatement) {
      return this.validate((AuthnStatement) statement, assertion, context);
    }
    else {
      throw new AssertionValidationException("Illegal call - statement is of type " + statement.getClass().getSimpleName());
    }
  }

  /**
   * Validates the {@link AuthnStatement}.
   * 
   * @param statement
   *          the statement to validate
   * @param assertion
   *          the assertion containing the statement
   * @param context
   *          validation context
   * @return validation result
   */
  protected ValidationResult validate(final AuthnStatement statement, final Assertion assertion, final ValidationContext context) {

    try {
      check(this.validateAuthnInstant(statement, assertion, context));
      check(this.validateSessionIndex(statement, assertion, context));
      check(this.validateSessionNotOnOrAfter(statement, assertion, context));
      check(this.validateSubjectLocality(statement, assertion, context));
      check(this.validateAuthnContext(statement, assertion, context));
    }
    catch (ValidationResultException e) {
      return e.getResult();
    }
    return ValidationResult.VALID;
  }

  /**
   * Validates the {@code AuthnInstant} of the {@code AuthnStatement}.
   * 
   * @param statement
   *          the statement
   * @param assertion
   *          the assertion containing the statement
   * @param context
   *          validation context
   * @return validation result
   */
  protected ValidationResult validateAuthnInstant(final AuthnStatement statement, final Assertion assertion, final ValidationContext context) {

    if (statement.getAuthnInstant() == null) {
      context.setValidationFailureMessage("AuthnInstant of Assertion/@AuthnStatement is missing");
      return ValidationResult.INVALID;
    }

    // Assert the the authentication instant is not newer than the assertion issuance time.
    //
    if (statement.getAuthnInstant().isAfter(assertion.getIssueInstant())) {
      context.setValidationFailureMessage("AuthnInstant is after assertion issue instant - invalid");
      return ValidationResult.INVALID;
    }

    // Make checks regarding SSO and session length ...
    //
    return this.validateSsoAndSession(statement.getAuthnInstant(), statement, assertion, context);
  }

  /**
   * Makes checks for SSO and session lengths.
   * 
   * @param authnInstant
   *          the authentication instant
   * @param statement
   *          the statement
   * @param assertion
   *          the assertion containing the statement
   * @param context
   *          validation context
   * @return validation result
   */
  protected ValidationResult validateSsoAndSession(final Instant authnInstant, final AuthnStatement statement, 
      final Assertion assertion, final ValidationContext context) {

    // If we requested a forced authentication, we check that the authentication instant is not before
    // the issuance time of the request.
    //
    Boolean forceAuthn = this.getForceAuthnFlag(context);
    Long authnRequestIssueInstant = this.getAuthnRequestIssueInstant(context);

    if (forceAuthn != null && forceAuthn.booleanValue()) {
      if (authnRequestIssueInstant != null) {
        if (authnInstant.toEpochMilli() + AbstractObjectValidator.getAllowedClockSkew(context) < authnRequestIssueInstant.longValue()) {
          String msg = String.format("Invalid Assertion. Force authentication was requested, but authentication "
              + "instant (%s) is before the issuance time of the authentication request (%s)",
            authnInstant, Instant.ofEpochMilli(authnRequestIssueInstant)); 
          context.setValidationFailureMessage(msg);
          return ValidationResult.INVALID;
        }
      }
      else {
        log.warn("%s (or %s) not suppplied - cannot check SSO", AUTHN_REQUEST_ISSUE_INSTANT, CoreValidatorParameters.AUTHN_REQUEST);
      }
    }
    else {
      // Forced authentication was not requested.

      // If we have specified the MAX_ACCEPTED_SSO_SESSION_TIME parameter we make a check that the
      // SSO session at the issuing IdP is not greater than what we can accept.
      //
      Long maxSessionTime = (Long) context.getStaticParameters().get(MAX_ACCEPTED_SSO_SESSION_TIME);
      if (maxSessionTime != null) {
        if (authnInstant.toEpochMilli() + maxSessionTime.longValue() < AbstractObjectValidator.getReceiveInstant(context)) {
          String msg = String.format(
            "Session length violation. Authentication instant (%s) is too far back in time to be accepted by SP SSO policy", authnInstant);
          context.setValidationFailureMessage(msg);
          return ValidationResult.INVALID;
        }

      }
    }

    return ValidationResult.VALID;
  }

  /**
   * Gets the {@code ForceAuthn} flag from the validation context. The method primarily checks for the
   * {@link #AUTHN_REQUEST_FORCE_AUTHN} parameter, and that does not exist, tries with the
   * {@link CoreValidatorParameters#AUTHN_REQUEST} parameter.
   * 
   * @param context
   *          the validation context
   * @return the {@code ForceAuthn} flag or {@code null} if this is not set
   */
  protected Boolean getForceAuthnFlag(final ValidationContext context) {
    Boolean forceAuthn = (Boolean) context.getStaticParameters().get(AUTHN_REQUEST_FORCE_AUTHN);
    if (forceAuthn == null) {
      AuthnRequest authnRequest = (AuthnRequest) context.getStaticParameters().get(CoreValidatorParameters.AUTHN_REQUEST);
      if (authnRequest != null) {
        forceAuthn = authnRequest.isForceAuthn();
      }
    }
    return forceAuthn;
  }

  /**
   * Gets the issue instant of the {@code AuthnRequest} from the validation context. The method primarily checks for the
   * {@link #AUTHN_REQUEST_ISSUE_INSTANT} parameter, and that does not exist, tries with the
   * {@link CoreValidatorParameters#AUTHN_REQUEST} parameter.
   * 
   * @param context
   *          the validation context
   * @return the issuance time
   */
  protected Long getAuthnRequestIssueInstant(final ValidationContext context) {
    Long issueInstant = (Long) context.getStaticParameters().get(AUTHN_REQUEST_ISSUE_INSTANT);
    if (issueInstant == null) {
      AuthnRequest authnRequest = (AuthnRequest) context.getStaticParameters().get(CoreValidatorParameters.AUTHN_REQUEST);
      if (authnRequest != null) {
        issueInstant = authnRequest.getIssueInstant().toEpochMilli();
      }
    }
    return issueInstant;
  }

  /**
   * Default implementation does not perform any checks and returns {@link ValidationResult#VALID}.
   * 
   * @param statement
   *          the statement
   * @param assertion
   *          the assertion
   * @param context
   *          the validation context
   * @return validation result
   */
  protected ValidationResult validateSessionIndex(final AuthnStatement statement, final Assertion assertion, final ValidationContext context) {
    return ValidationResult.VALID;
  }

  /**
   * Default implementation does not perform any checks and returns {@link ValidationResult#VALID}.
   * 
   * @param statement
   *          the statement
   * @param assertion
   *          the assertion
   * @param context
   *          the validation context
   * @return validation result
   */
  protected ValidationResult validateSessionNotOnOrAfter(final AuthnStatement statement, final Assertion assertion, final ValidationContext context) {
    return ValidationResult.VALID;
  }

  /**
   * Default implementation does not perform any checks and returns {@link ValidationResult#VALID}.
   * 
   * @param statement
   *          the statement
   * @param assertion
   *          the assertion
   * @param context
   *          the validation context
   * @return validation result
   */
  protected ValidationResult validateSubjectLocality(final AuthnStatement statement, final Assertion assertion, final ValidationContext context) {
    return ValidationResult.VALID;
  }

  /**
   * Default implementation will only assert that the {@code AuthnContext} element is present.
   * 
   * @param statement
   *          the statement
   * @param assertion
   *          the assertion
   * @param context
   *          the validation context
   * @return validation result
   */
  protected ValidationResult validateAuthnContext(final AuthnStatement statement, final Assertion assertion, final ValidationContext context) {
    if (statement.getAuthnContext() == null) {
      context.setValidationFailureMessage("AuthnContext element is missing in Assertion/@AuthnStatement");
      return ValidationResult.INVALID;
    }
    return ValidationResult.VALID;
  }

}
