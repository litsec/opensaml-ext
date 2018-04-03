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
package se.litsec.opensaml.saml2.common.assertion;

import static se.litsec.opensaml.common.validation.ValidationSupport.check;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.ConditionValidator;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;
import org.opensaml.saml.saml2.assertion.StatementValidator;
import org.opensaml.saml.saml2.assertion.SubjectConfirmationValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Condition;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.Statement;
import org.opensaml.saml.saml2.core.Subject;
import org.opensaml.saml.saml2.core.SubjectConfirmation;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import se.litsec.opensaml.common.validation.AbstractObjectValidator;
import se.litsec.opensaml.common.validation.AbstractSignableObjectValidator;
import se.litsec.opensaml.common.validation.CoreValidatorParameters;
import se.litsec.opensaml.common.validation.ValidationSupport.ValidationResultException;

/**
 * A validator for {@code Assertion} objects.
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * </p>
 * <ul>
 * <li>The static parameters defined for {@link AbstractSignableObjectValidator}.</li>
 * <li>{@link CoreValidatorParameters#STRICT_VALIDATION}: Optional. If not supplied, defaults to 'false'. Tells whether
 * strict validation should be performed.</li>
 * <li>{@link CoreValidatorParameters#ALLOWED_CLOCK_SKEW}: Optional. Gives the number of milliseconds that is the
 * maximum allowed clock skew. If not given {@link AbstractObjectValidator#DEFAULT_ALLOWED_CLOCK_SKEW} is used.</li>
 * <li>{@link CoreValidatorParameters#MAX_AGE_MESSAGE}: Optional. Gives the maximum age (difference between issuance
 * time and the validation time). If not given, the {@link AbstractObjectValidator#DEFAULT_MAX_AGE_RECEIVED_MESSAGE} is
 * used.</li>
 * <li>{@link CoreValidatorParameters#RECEIVE_INSTANT}: Optional. Gives the timestamp (milliseconds since epoch) for
 * when the response message was received. If not given the current time is used.</li>
 * <li>{@link CoreValidatorParameters#AUTHN_REQUEST}: Optional. If supplied will be used in a number of validations when
 * information from the corresponding {@code AuthnRequest} is needed. If not supplied, other, more detailed parameters
 * must be given.</li>
 * <li>{@link CoreValidatorParameters#AUTHN_REQUEST_ID}: Required if the {@link CoreValidatorParameters#AUTHN_REQUEST}
 * is not assigned. Is used when validating the {@code InResponseTo} attribute of the response.</li>
 * <li>{@link CoreValidatorParameters#RECEIVE_URL}: Required. A String holding the URL on which we received the response
 * message. Is used when the {@code Destination} attribute is validated.</li>
 * <li>{@link CoreValidatorParameters#EXPECTED_ISSUER}: Optional. If set, is used when the issuer of the response is
 * validated. If not set, the issuer from the {@link CoreValidatorParameters#AUTHN_REQUEST} is used (if available).</li>
 * <li>{@link #RESPONSE_ISSUE_INSTANT}: Optional. If set, the IssueInstant of the Assertion being validated is compared
 * with the corresponding response issue instant.</li>
 * </ul>
 * 
 * <p>
 * Supports the following {@link ValidationContext} dynamic parameters:
 * </p>
 * <ul>
 * <li>{@link SAML2AssertionValidationParameters#CONFIRMED_SUBJECT_CONFIRMATION}: Optional. Will be present after
 * validation if subject confirmation was successfully performed.</li>
 * </ul>
 * 
 * <p>
 * <b>Note:</b> Also check the validation context parameters defined by the {@code SubjectConfirmationValidator} and
 * {@code ConditionValidator} instances that are installed.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AssertionValidator extends AbstractSignableObjectValidator<Assertion> {

  /**
   * Carries a {@link Long} holding the issue instant of the Response that contained the assertion being validated.
   */
  public static final String RESPONSE_ISSUE_INSTANT = CoreValidatorParameters.STD_PREFIX + ".ResponseIssueInstant";

  /** Class logger. */
  private final Logger log = LoggerFactory.getLogger(AssertionValidator.class);

  /** Registered {@link SubjectConfirmation} validators. */
  protected Map<String, SubjectConfirmationValidator> subjectConfirmationValidators;

  /** Registered {@link Condition} validators. */
  protected Map<QName, ConditionValidator> conditionValidators;

  /** Registered {@link Statement} validators. */
  private Map<QName, StatementValidator> statementValidators;

  /**
   * Constructor.
   * 
   * @param trustEngine
   *          the trust used to validate the object's signature
   * @param signaturePrevalidator
   *          the signature pre-validator used to pre-validate the object's signature
   * @param confirmationValidators
   *          validators used to validate {@link SubjectConfirmation} methods within the assertion
   * @param conditionValidators
   *          validators used to validate the {@link Condition} elements within the assertion
   * @param statementValidators
   *          validators used to validate {@link Statement}s within the assertion
   */
  public AssertionValidator(SignatureTrustEngine trustEngine, SignaturePrevalidator signaturePrevalidator,
      Collection<SubjectConfirmationValidator> confirmationValidators,
      Collection<ConditionValidator> conditionValidators,
      Collection<StatementValidator> statementValidators) {
    super(trustEngine, signaturePrevalidator);

    this.subjectConfirmationValidators = new HashMap<>();
    if (confirmationValidators != null) {
      for (SubjectConfirmationValidator validator : confirmationValidators) {
        if (validator != null) {
          this.subjectConfirmationValidators.put(validator.getServicedMethod(), validator);
        }
      }
    }

    this.conditionValidators = new HashMap<>();
    if (conditionValidators != null) {
      for (ConditionValidator validator : conditionValidators) {
        if (validator != null) {
          this.conditionValidators.put(validator.getServicedCondition(), validator);
        }
      }
    }

    this.statementValidators = new HashMap<>();
    if (statementValidators != null) {
      for (StatementValidator validator : statementValidators) {
        if (validator != null) {
          this.statementValidators.put(validator.getServicedStatement(), validator);
        }
      }
    }

  }

  /**
   * Validates the assertion.
   */
  @Override
  public ValidationResult validate(Assertion assertion, ValidationContext context) {
    try {
      check(this.validateID(assertion, context));
      check(this.validateVersion(assertion, context));
      check(this.validateIssueInstant(assertion, context));
      check(this.validateIssuer(assertion, context));
      check(this.validateSignature(assertion, context));
      check(this.validateSubject(assertion, context));
      check(this.validateConditions(assertion, context));
      check(this.validateStatements(assertion, context));
    }
    catch (ValidationResultException e) {
      return e.getResult();
    }
    return ValidationResult.VALID;
  }

  /**
   * Validates that the {@code Assertion} object has an ID attribute.
   * 
   * @param assertion
   *          the assertion
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateID(Assertion assertion, ValidationContext context) {
    if (!StringUtils.hasText(assertion.getID())) {
      context.setValidationFailureMessage("Missing ID attribute in Assertion");
      return ValidationResult.INVALID;
    }
    return ValidationResult.VALID;
  }

  /**
   * Validates that the {@code Response} object has a valid Version attribute.
   * 
   * @param assertion
   *          the assertion
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateVersion(Assertion assertion, ValidationContext context) {
    if (assertion.getVersion() == null || !assertion.getVersion().toString().equals(SAMLVersion.VERSION_20.toString())) {
      context.setValidationFailureMessage("Invalid SAML version in Assertion");
      return ValidationResult.INVALID;
    }
    return ValidationResult.VALID;
  }

  /**
   * Validates that the {@code Assertion} object has a IssueInstant attribute and checks that its value is OK. If the
   * response that contained the assertion was previously validated the static context parameter
   * {@link #RESPONSE_ISSUE_INSTANT} should be passed. If so, the method checks that the assertion issue instant is not
   * after the response issue instant. Otherwise the method checks that the IssueInstant is not too old given the
   * {@link CoreValidatorParameters#MAX_AGE_MESSAGE} and {@link CoreValidatorParameters#RECEIVE_INSTANT} context
   * parameters.
   * 
   * @param assertion
   *          the response
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateIssueInstant(Assertion assertion, ValidationContext context) {
    if (assertion.getIssueInstant() == null) {
      context.setValidationFailureMessage("Missing IssueInstant attribute in Assertion");
      return ValidationResult.INVALID;
    }

    // Is the response issue instance specified? If so, we only check that the assertion issue instant
    // is before the response issue instant. In these cases we assume that the response issue instant
    // has been verified.
    //
    Long responseIssueInstant = (Long) context.getStaticParameters().get(RESPONSE_ISSUE_INSTANT);
    if (responseIssueInstant != null) {
      if (assertion.getIssueInstant().isAfter(responseIssueInstant)) {
        final String msg = String.format("Invalid Assertion - Its issue-instant (%s) is after the response message issue-instant (%s)",
          assertion.getIssueInstant(), new DateTime(responseIssueInstant, ISOChronology.getInstanceUTC()));
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
      }
    }
    else {
      // Otherwise, we have to make more checks.

      final long receiveInstant = getReceiveInstant(context);
      final long issueInstant = assertion.getIssueInstant().getMillis();

      final long maxAgeResponse = getMaxAgeReceivedMessage(context);
      final long allowedClockSkew = getAllowedClockSkew(context);

      // Too old?
      //
      if ((receiveInstant - issueInstant) > (maxAgeResponse + allowedClockSkew)) {
        final String msg = String.format("Received Assertion is too old - issue-instant: %s - receive-time: %s",
          assertion.getIssueInstant(), new DateTime(receiveInstant, ISOChronology.getInstanceUTC()));
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
      }

      // Not yet valid? -> Clock skew is unacceptable.
      //
      if ((issueInstant - receiveInstant) > allowedClockSkew) {
        final String msg = String.format("Issue-instant of Assertion (%s) is newer than receive time (%s) - Non accepted clock skew",
          assertion.getIssueInstant(), new DateTime(receiveInstant, ISOChronology.getInstanceUTC()));
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
      }
    }

    return ValidationResult.VALID;
  }

  /**
   * Ensures that the {@code Issuer} element is present and matches the expected issuer (if set in the context under the
   * {@link CoreValidatorParameters#EXPECTED_ISSUER} key).
   * 
   * @param assertion
   *          the assertion
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateIssuer(Assertion assertion, ValidationContext context) {
    if (assertion.getIssuer() == null || assertion.getIssuer().getValue() == null) {
      context.setValidationFailureMessage("Missing Issuer element in Assertion");
      return ValidationResult.INVALID;
    }
    String expectedIssuer = (String) context.getStaticParameters().get(CoreValidatorParameters.EXPECTED_ISSUER);
    if (expectedIssuer != null) {
      if (!assertion.getIssuer().getValue().equals(expectedIssuer)) {
        final String msg = String.format("Issuer of Assertion (%s) did not match expected issuer (%s)",
          assertion.getIssuer().getValue(), expectedIssuer);
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
      }
    }
    else {
      log.warn("EXPECTED_ISSUER key not set - will not check issuer of Assertion");
    }

    return ValidationResult.VALID;
  }

  /**
   * Validates the {@code Subject} element of the assertion. The default implementation returns
   * {@link ValidationResult#VALID} if there is no {@code Subject} element since it is optional according to the SAML
   * 2.0 Core specifications.
   * 
   * @param assertion
   *          the assertion
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateSubject(Assertion assertion, ValidationContext context) {
    if (assertion.getSubject() == null) {
      
      // Assertions containing AuthnStatements must contain a Subject.
      //
      if (assertion.getAuthnStatements() != null && !assertion.getAuthnStatements().isEmpty()) {
        context.setValidationFailureMessage("Assertion contains AuthnStatement but no Subject - invalid");
        return ValidationResult.INVALID;
      }
      
      log.debug("Assertion does not contain a Subject element - allowed by default assertion validator");
      return ValidationResult.VALID;
    }
    final Subject subject = assertion.getSubject();
    List<SubjectConfirmation> confirmations = subject.getSubjectConfirmations();
    if (confirmations == null || confirmations.isEmpty()) {
      log.debug("Assertion contains no SubjectConfirmations, default assertion validator skips subject confirmation");
      return ValidationResult.VALID;
    }

    return this.validateSubjectConfirmations(assertion, confirmations, context);
  }

  /**
   * Validates the subject confirmations and for the one that is confirmed, it is saved in the validation context under
   * the {@link SAML2AssertionValidationParameters#CONFIRMED_SUBJECT_CONFIRMATION} key.
   * 
   * @param assertion
   *          the assertion
   * @param subjectConfirmations
   *          the subject confirmations
   * @param context
   *          the validation context
   * @return a validation result
   */
  protected ValidationResult validateSubjectConfirmations(Assertion assertion, List<SubjectConfirmation> subjectConfirmations,
      ValidationContext context) {

    for (SubjectConfirmation confirmation : subjectConfirmations) {
      SubjectConfirmationValidator validator = subjectConfirmationValidators.get(confirmation.getMethod());
      if (validator != null) {
        try {
          ValidationResult r = validator.validate(confirmation, assertion, context);
          if (r == ValidationResult.VALID) {
            context.getDynamicParameters().put(
              SAML2AssertionValidationParameters.CONFIRMED_SUBJECT_CONFIRMATION, confirmation);
            return ValidationResult.VALID;
          }
          else {
            log.info("Validation of SubjectConfirmation with method '{}' failed - {}", confirmation.getMethod(), context
              .getValidationFailureMessage());
          }
        }
        catch (AssertionValidationException e) {
          log.warn("Error while executing subject confirmation validation " + validator.getClass().getName(), e);
        }
      }
    }

    String msg = String.format("No subject confirmation methods were met for assertion with ID '%s'", assertion.getID());
    log.debug(msg);
    context.setValidationFailureMessage(msg);
    return ValidationResult.INVALID;
  }

  /**
   * Validates the {@code Conditions} elements of the assertion.
   * 
   * @param assertion
   *          the assertion
   * @param context
   *          the validation context
   * @return the validation result
   */
  protected ValidationResult validateConditions(Assertion assertion, ValidationContext context) {

    Conditions conditions = assertion.getConditions();
    if (conditions == null) {
      log.debug("Assertion contained no Conditions element - allowed by default assertion validator");
      return ValidationResult.VALID;
    }

    ValidationResult timeboundsResult = this.validateConditionsTimeBounds(assertion, context);
    if (timeboundsResult != ValidationResult.VALID) {
      return timeboundsResult;
    }

    for (Condition condition : conditions.getConditions()) {
      ConditionValidator validator = conditionValidators.get(condition.getElementQName());
      if (validator == null && condition.getSchemaType() != null) {
        validator = conditionValidators.get(condition.getSchemaType());
      }

      if (validator == null) {
        final String msg = String.format("Unknown Condition '%s' of type '%s' in assertion '%s'",
          condition.getElementQName(), condition.getSchemaType(), assertion.getID());
        log.warn(msg);
        if (isStrictValidation(context)) {
          context.setValidationFailureMessage(msg);
          return ValidationResult.INDETERMINATE;
        }
        else {
          continue;
        }
      }

      ValidationResult r;
      try {
        r = validator.validate(condition, assertion, context);
      }
      catch (AssertionValidationException e) {
        log.error("Failed Conditions validation - {}", e.getMessage());
        log.debug("", e);
        context.setValidationFailureMessage(e.getMessage());
        r = ValidationResult.INVALID;
      }

      if (r != ValidationResult.VALID) {
        String msg = String.format("Condition '%s' of type '%s' in assertion '%s' was not valid - %s.",
          condition.getElementQName(), condition.getSchemaType(), assertion.getID(), context.getValidationFailureMessage());
        if (context.getValidationFailureMessage() != null) {
          msg = msg + ": " + context.getValidationFailureMessage();
        }
        log.debug(msg);
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
      }
    }

    return ValidationResult.VALID;
  }

  /**
   * Validates the NotBefore and NotOnOrAfter Conditions constraints on the assertion.
   * 
   * @param assertion
   *          the assertion whose conditions will be validated
   * @param context
   *          current validation context
   * @return the result of the validation evaluation
   */
  protected ValidationResult validateConditionsTimeBounds(Assertion assertion, ValidationContext context) {

    Conditions conditions = assertion.getConditions();
    if (conditions == null) {
      return ValidationResult.VALID;
    }

    long clockSkew = getAllowedClockSkew(context);
    Long _receiveInstant = (Long) context.getStaticParameters().get(CoreValidatorParameters.RECEIVE_INSTANT);
    DateTime receiveInstant = _receiveInstant != null ? new DateTime(_receiveInstant, ISOChronology.getInstanceUTC())
        : new DateTime(ISOChronology.getInstanceUTC());

    DateTime notBefore = conditions.getNotBefore();
    log.debug("Evaluating Conditions NotBefore '{}' against 'skewed now' time '{}'", notBefore, receiveInstant.plus(clockSkew));
    if (notBefore != null && notBefore.isAfter(receiveInstant.plus(clockSkew))) {
      context.setValidationFailureMessage(String.format(
        "Assertion '%s' with NotBefore condition of '%s' is not yet valid", assertion.getID(), notBefore));
      return ValidationResult.INVALID;
    }

    DateTime notOnOrAfter = conditions.getNotOnOrAfter();
    log.debug("Evaluating Conditions NotOnOrAfter '{}' against 'skewed now' time '{}'", notOnOrAfter, receiveInstant.minus(clockSkew));
    if (notOnOrAfter != null && notOnOrAfter.isBefore(receiveInstant.minus(clockSkew))) {
      context.setValidationFailureMessage(String.format(
        "Assertion '%s' with NotOnOrAfter condition of '%s' is no longer valid", assertion.getID(), notOnOrAfter));
      return ValidationResult.INVALID;
    }

    return ValidationResult.VALID;
  }

  /**
   * Validates the statements of the assertion using the registered {@link StatementValidator} instance.
   * 
   * @param assertion
   *          the assertion to validate
   * @param context
   *          the validation context
   * @return validation result
   */
  protected ValidationResult validateStatements(Assertion assertion, ValidationContext context) {

    List<Statement> statements = assertion.getStatements();
    if (statements == null || statements.isEmpty()) {
      return ValidationResult.VALID;
    }

    StatementValidator validator;
    for (Statement statement : statements) {
      validator = statementValidators.get(statement.getElementQName());
      if (validator == null && statement.getSchemaType() != null) {
        validator = statementValidators.get(statement.getSchemaType());
      }

      if (validator != null) {
        ValidationResult result;
        try {
          result = validator.validate(statement, assertion, context);
        }
        catch (AssertionValidationException e) {
          log.error("Failed Statement validation - {}", e.getMessage());
          log.debug("", e);
          context.setValidationFailureMessage(e.getMessage());
          result = ValidationResult.INVALID;
        }
        if (result != ValidationResult.VALID) {
          return result;
        }
      }
    }

    return ValidationResult.VALID;
  }

  /**
   * Returns the Assertion issuer.
   */
  @Override
  protected String getIssuer(Assertion signableObject) {
    return signableObject.getIssuer() != null ? signableObject.getIssuer().getValue() : null;
  }

  /**
   * Returns the Assertion ID.
   */
  @Override
  protected String getID(Assertion signableObject) {
    return signableObject.getID();
  }

  /** {@inheritDoc} */
  @Override
  protected String getObjectName() {
    return "Assertion";
  }

}
