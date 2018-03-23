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

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.security.SecurityException;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.signature.SignableXMLObject;
import org.opensaml.xmlsec.signature.Signature;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;

/**
 * Abstract object validator that supports validating signatures.
 * 
 * <p>
 * Supports the following {@link ValidationContext} static parameters:
 * <ul>
 * <li>The static parameters defined in {@link AbstractObjectValidator}.
 * </li>
 * <li>{@link CoreValidatorParameters#SIGNATURE_REQUIRED}: Optional. 
 * If not supplied, defaults to 'true'. If an object is signed, the signature is always evaluated and the result 
 * factored into the overall validation result, regardless of the value of this setting.
 * </li>
 * <li>{@link CoreValidatorParameters#SIGNATURE_VALIDATION_CRITERIA_SET}: Optional. 
 * If not supplied, a minimal criteria set will be constructed which contains an {@link EntityIDCriteria} 
 * containing the Issuer entityID, and a {@link UsageCriteria} of {@link UsageType#SIGNING}. If it is supplied, 
 * but either of those criteria are absent from the criteria set, they will be added with the above values.
 * </li>
 * </ul>
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 *
 * @param <T>
 *          the type of the object that is to be validated
 */
public abstract class AbstractSignableObjectValidator<T extends SignableXMLObject> extends AbstractObjectValidator<T> {

  /** Class logger. */
  private final Logger log = LoggerFactory.getLogger(AbstractSignableObjectValidator.class);

  /** Trust engine for signature evaluation. */
  protected SignatureTrustEngine trustEngine;

  /** SAML signature profile validator. */
  protected SignaturePrevalidator signaturePrevalidator;

  /**
   * Constructor.
   * 
   * @param trustEngine
   *          the trust used to validate the object's signature
   * @param signaturePrevalidator
   *          the signature pre-validator used to pre-validate the object's signature
   */
  public AbstractSignableObjectValidator(SignatureTrustEngine trustEngine, SignaturePrevalidator signaturePrevalidator) {
    this.trustEngine = trustEngine;
    this.signaturePrevalidator = signaturePrevalidator;
  }

  /**
   * Validates the signature of the assertion, if it is signed.
   * 
   * @param token
   *          assertion whose signature will be validated
   * @param context
   *          current validation context
   * 
   * @return the result of the signature validation
   * 
   * @throws AssertionValidationException
   *           thrown if there is a problem determining the validity of the signature
   */
  protected ValidationResult validateSignature(T token, ValidationContext context) {

    Boolean signatureRequired = (Boolean) context.getStaticParameters().get(CoreValidatorParameters.SIGNATURE_REQUIRED);
    if (signatureRequired == null) {
      signatureRequired = Boolean.TRUE;
    }

    // Validate params and requirements.
    if (!token.isSigned()) {
      if (signatureRequired) {
        context.setValidationFailureMessage(String.format("%s was required to be signed, but was not", this.getObjectName()));
        return ValidationResult.INVALID;
      }
      else {
        log.debug(String.format("%s was not required to be signed, and was not signed. Skipping further signature evaluation",
          this.getObjectName()));
        return ValidationResult.VALID;
      }
    }

    if (trustEngine == null) {
      log.warn("Signature validation was necessary, but no signature trust engine was available");
      context.setValidationFailureMessage(String.format(
        "%s signature could not be evaluated due to internal error", this.getObjectName()));
      return ValidationResult.INDETERMINATE;
    }

    return this.performSignatureValidation(token, context);
  }

  /**
   * Handles the actual signature validation.
   * 
   * @param token
   *          object whose signature will be validated
   * @param context
   *          current validation context
   * 
   * @return the validation result
   */
  protected ValidationResult performSignatureValidation(T token, ValidationContext context) {
    
    // Temporary code until we figure out how to make the OpenSAML unmarshaller to
    // mark the ID attribute as an ID.
    //
    Attr idAttr = token.getDOM().getAttributeNode("ID");
    if (idAttr != null) {
      idAttr.getOwnerElement().setIdAttributeNode(idAttr, true);
    }    
    
    Signature signature = token.getSignature();

    String tokenIssuer = this.getIssuer(token);

    log.debug("Attempting signature validation on {} '{}' from Issuer '{}'",
      this.getObjectName(), this.getID(token), tokenIssuer);

    try {
      signaturePrevalidator.validate(signature);
    }
    catch (SignatureException e) {
      String msg = String.format("%s Signature failed pre-validation: %s", this.getObjectName(), e.getMessage());
      log.warn(msg);
      context.setValidationFailureMessage(msg);
      return ValidationResult.INVALID;
    }

    CriteriaSet criteriaSet = this.getSignatureValidationCriteriaSet(token, context);

    try {
      if (trustEngine.validate(signature, criteriaSet)) {
        log.debug("Validation of signature of {} '{}' from Issuer '{}' was successful",
          this.getObjectName(), this.getID(token), tokenIssuer);
        return ValidationResult.VALID;
      }
      else {
        String msg = String.format(
          "Signature of %s '%s' from Issuer '%s' was not valid", this.getObjectName(), this.getID(token), tokenIssuer);
        log.warn(msg);
        context.setValidationFailureMessage(msg);
        return ValidationResult.INVALID;
      }
    }
    catch (SecurityException e) {
      String msg = String.format("A problem was encountered evaluating the signature over %s with ID '%s': %s",
        this.getObjectName(), this.getID(token), e.getMessage());
      log.warn(msg);
      context.setValidationFailureMessage(msg);
      return ValidationResult.INVALID;
    }

  }

  /**
   * Get the criteria set that will be used in evaluating the Assertion signature via the supplied trust engine.
   * 
   * @param token
   *          object whose signature will be validated
   * @param context
   *          current validation context
   * @return the criteria set to use
   */
  protected CriteriaSet getSignatureValidationCriteriaSet(T token, ValidationContext context) {

    CriteriaSet criteriaSet = (CriteriaSet) context.getStaticParameters().get(
      CoreValidatorParameters.SIGNATURE_VALIDATION_CRITERIA_SET);
    if (criteriaSet == null) {
      criteriaSet = new CriteriaSet();
    }

    if (!criteriaSet.contains(EntityIdCriterion.class)) {
      String issuer = this.getIssuer(token);
      if (issuer != null) {
        criteriaSet.add(new EntityIdCriterion(issuer));
      }
    }

    if (!criteriaSet.contains(UsageCriterion.class)) {
      criteriaSet.add(new UsageCriterion(UsageType.SIGNING));
    }

    return criteriaSet;
  }

  /**
   * Returns the issuer of the signable object.
   * 
   * @param signableObject
   *          the object being verified
   * @return the issuer
   */
  protected abstract String getIssuer(T signableObject);

  /**
   * Returns the ID of the signable object.
   * 
   * @param signableObject
   *          the object being verified
   * @return the ID
   */
  protected abstract String getID(T signableObject);

  /**
   * Returns the name of the object being validated, e.g. "Assertion". Uses for logging.
   * 
   * @return the object name
   */
  protected abstract String getObjectName();

}
