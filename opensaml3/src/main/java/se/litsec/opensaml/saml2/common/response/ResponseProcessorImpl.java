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

import java.io.ByteArrayInputStream;
import java.util.Optional;

import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.messaging.decoder.MessageDecodingException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.saml.saml2.core.StatusCode;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.security.impl.MetadataCredentialResolver;
import org.opensaml.saml.security.impl.SAMLSignatureProfileValidator;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.config.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.encryption.support.DecryptionException;
import org.opensaml.xmlsec.signature.support.SignaturePrevalidator;
import org.opensaml.xmlsec.signature.support.SignatureTrustEngine;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
import se.litsec.opensaml.saml2.common.assertion.AssertionValidationParametersBuilder;
import se.litsec.opensaml.saml2.common.assertion.AssertionValidator;
import se.litsec.opensaml.saml2.metadata.PeerMetadataResolver;
import se.litsec.opensaml.utils.ObjectUtils;
import se.litsec.opensaml.xmlsec.SAMLObjectDecrypter;

/**
 * Response processor for SAML Response messages.
 * <p>
 * Note that {@link #initialize()} must be invoked before the bean can be used.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ResponseProcessorImpl implements ResponseProcessor {

  /** Logging instance. */
  private final Logger log = LoggerFactory.getLogger(ResponseProcessorImpl.class);

  /** The decrypter instance. */
  protected SAMLObjectDecrypter decrypter;

  /** The replay checker. */
  protected MessageReplayChecker messageReplayChecker;

  /** Used to locate certificates from the IdP metadata. */
  protected MetadataCredentialResolver metadataCredentialResolver;

  /** The signature trust engine to be used when validating signatures. */
  protected SignatureTrustEngine signatureTrustEngine;

  /** Validator for checking the a Signature is correct with respect to the standards. */
  protected SignaturePrevalidator signatureProfileValidator = new SAMLSignatureProfileValidator();

  /** The response validator. */
  protected ResponseValidator responseValidator;

  /** The assertion validator. */
  protected AssertionValidator assertionValidator;

  /** Static response validation settings. */
  protected ResponseValidationSettings responseValidationSettings;

  /** Is this component initialized? */
  private boolean isInitialized = false;

  /** {@inheritDoc} */
  @Override
  public ResponseProcessingResult processSamlResponse(String samlResponse, String relayState, ResponseProcessingInput input,
      PeerMetadataResolver peerMetadataResolver, ValidationContext validationContext) throws ResponseStatusErrorException,
      ResponseProcessingException {

    try {
      // Step 1: Decode the SAML response message.
      //
      Response response = this.decodeResponse(samlResponse);

      if (log.isTraceEnabled()) {
        log.trace("[{}] Decoded Response: {}", logId(response), ObjectUtils.toStringSafe(response));
      }

      // The IdP metadata is required for all steps below ...
      //
      final String issuer = response.getIssuer() != null ? response.getIssuer().getValue() : null;
      final EntityDescriptor idpMetadata = issuer != null ? peerMetadataResolver.getMetadata(issuer) : null;

      // Step 2: Validate the Response (including its signature).
      //
      this.validateResponse(response, relayState, input, idpMetadata, validationContext);

      // Step 3: Make sure this isn't a replay attack
      //
      this.messageReplayChecker.checkReplay(response);

      // Step 4. Check Status
      //
      if (!StatusCode.SUCCESS.equals(response.getStatus().getStatusCode().getValue())) {
        log.info("Authentication failed with status '{}' [{}]", ResponseStatusErrorException.statusToString(response.getStatus()), logId(
          response));
        throw new ResponseStatusErrorException(response.getStatus(), response.getID());
      }

      // Step 5. Verify that the relay state matches the request.
      //
      this.validateRelayState(response, relayState, input);

      // Step 6. Decrypt assertion
      //
      Assertion assertion = this.decrypter.decrypt(response.getEncryptedAssertions().get(0), Assertion.class);
      if (log.isTraceEnabled()) {
        log.trace("[{}] Decrypted Assertion: {}", logId(response, assertion), ObjectUtils.toStringSafe(assertion));
      }

      // Step 7. Validate the assertion
      //
      this.validateAssertion(assertion, response, input, idpMetadata, validationContext);

      // And finally, build the result.
      //
      return new ResponseProcessingResultImpl(assertion);
    }
    catch (MessageReplayException e) {
      throw new ResponseProcessingException("Message replay: " + e.getMessage(), e);
    }
    catch (DecryptionException e) {
      throw new ResponseProcessingException("Failed to decrypt assertion: " + e.getMessage(), e);
    }
  }

  /**
   * Initializes the component.
   * 
   * @throws Exception
   *           for initialization errors
   */
  public void initialize() throws Exception {
    Assert.notNull(this.decrypter, "Property 'decrypter' must be assigned");
    Assert.notNull(this.messageReplayChecker, "Property 'messageReplayChecker' must be assigned");

    if (this.responseValidationSettings == null) {
      this.responseValidationSettings = new ResponseValidationSettings();
      log.info("Using default responseValidationSettings [{}]", this.responseValidationSettings);
    }

    if (!this.isInitialized) {

      this.metadataCredentialResolver = new MetadataCredentialResolver();
      this.metadataCredentialResolver.setKeyInfoCredentialResolver(DefaultSecurityConfigurationBootstrap
        .buildBasicInlineKeyInfoCredentialResolver());
      this.metadataCredentialResolver.initialize();

      this.signatureTrustEngine = new ExplicitKeySignatureTrustEngine(this.metadataCredentialResolver,
        DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver());

      this.responseValidator = this.createResponseValidator(signatureTrustEngine, signatureProfileValidator);
      Assert.notNull(this.responseValidator, "createResponseValidator must not return null");
      this.assertionValidator = this.createAssertionValidator(signatureTrustEngine, signatureProfileValidator);
      Assert.notNull(this.assertionValidator, "createAssertionValidator must not return null");

      this.isInitialized = true;
    }
  }

  /**
   * Sets up the response validator.
   * <p>
   * The default implementation creates a {@link ResponseValidator} instance. For use within the Swedish eID framework
   * subclasses should create a {@code SwedishEidResponseValidator} instance, see the swedish-eid-opensaml library
   * (https://github.com/litsec/swedish-eid-opensaml).
   * </p>
   * 
   * @param signatureTrustEngine
   *          the signature trust engine to be used when validating signatures
   * @param signatureProfileValidator
   *          validator for checking the a Signature is correct with respect to the standards
   * @return the created response validator
   */
  protected ResponseValidator createResponseValidator(SignatureTrustEngine signatureTrustEngine,
      SignaturePrevalidator signatureProfileValidator) {
    return new ResponseValidator(signatureTrustEngine, signatureProfileValidator);
  }

  /**
   * Sets up the assertion validator.
   * <p>
   * The default implementation creates a {@link AssertionValidator} instance. For use within the Swedish eID framework
   * subclasses should create a {@code SwedishEidAssertionValidator} instance, see the swedish-eid-opensaml library
   * (https://github.com/litsec/swedish-eid-opensaml).
   * </p>
   * 
   * @param signatureTrustEngine
   *          the signature trust engine to be used when validating signatures
   * @param signatureProfileValidator
   *          validator for checking the a Signature is correct with respect to the standards
   * @return the created assertion validator
   */
  protected AssertionValidator createAssertionValidator(SignatureTrustEngine signatureTrustEngine,
      SignaturePrevalidator signatureProfileValidator) {
    return null;
  }

  /**
   * Decodes the received SAML response message into a {@link Response} object.
   * 
   * @param samlResponse
   *          the Base64 encoded SAML response
   * @return a {@code Response} object
   * @throws ResponseProcessingException
   *           for decoding errors
   */
  protected Response decodeResponse(String samlResponse) throws ResponseProcessingException {
    try {
      final byte[] decodedBytes = Base64Support.decode(samlResponse);
      if (decodedBytes == null) {
        log.error("Unable to Base64 decode SAML response message");
        throw new MessageDecodingException("Unable to Base64 decode SAML response message");
      }
      return ObjectUtils.unmarshall(new ByteArrayInputStream(decodedBytes), Response.class);
    }
    catch (MessageDecodingException | XMLParserException | UnmarshallingException e) {
      throw new ResponseProcessingException("Failed to decode message", e);
    }
  }

  /**
   * Validates the response including its signature.
   * 
   * @param response
   *          the response to verify
   * @param relayState
   *          the relay state that was received
   * @param input
   *          the processing input
   * @param idpMetadata
   *          the IdP metadata
   * @param validationContext
   *          optional validation context
   * @throws ResponseValidationException
   *           for validation errors
   */
  protected void validateResponse(Response response, String relayState, ResponseProcessingInput input, EntityDescriptor idpMetadata,
      ValidationContext validationContext)
      throws ResponseValidationException {

    if (input.getAuthnRequest() == null) {
      String msg = String.format("No AuthnRequest available when processing Response [%s]", logId(response));
      log.error("{}", msg);
      throw new ResponseValidationException(msg);
    }

    IDPSSODescriptor descriptor = idpMetadata != null ? idpMetadata.getIDPSSODescriptor(SAMLConstants.SAML20P_NS) : null;
    if (descriptor == null) {
      throw new ResponseValidationException("Invalid/missing IdP metadata - cannot verify Response signature");
    }

    ResponseValidationParametersBuilder b = ResponseValidationParametersBuilder.builder()
      .strictValidation(this.responseValidationSettings.isStrictValidation())
      .allowedClockSkew(this.responseValidationSettings.getAllowedClockSkew())
      .maxAgeReceivedMessage(this.responseValidationSettings.getMaxAgeResponse())
      .signatureRequired(Boolean.TRUE)
      .signatureValidationCriteriaSet(new CriteriaSet(new RoleDescriptorCriterion(descriptor), new UsageCriterion(UsageType.SIGNING)))
      .expectedIssuer(idpMetadata.getEntityID())
      .receiveInstant(input.getReceiveInstant())
      .receiveUrl(input.getReceiveURL())
      .authnRequest(input.getAuthnRequest());

    if (validationContext != null) {
      b.addStaticParameters(validationContext.getStaticParameters());
      b.addDynamicParameters(validationContext.getDynamicParameters());
    }
    ValidationContext context = b.build();

    ValidationResult result = this.responseValidator.validate(response, context);
    if (validationContext != null) {
      validationContext.getDynamicParameters().putAll(context.getDynamicParameters());
    }
    switch (result) {
    case VALID:
      log.debug("Response was successfully validated [{}]", logId(response));
      break;
    case INDETERMINATE:
      log.warn("Validation of Response was indeterminate - {} [{}]", context.getValidationFailureMessage(), logId(response));
      break;
    case INVALID:
      log.error("Validation of Response failed - {} [{}]", context.getValidationFailureMessage(), logId(response));
      throw new ResponseValidationException(context.getValidationFailureMessage());
    }
  }

  /**
   * Validates the received relay state matches what we sent.
   * 
   * @param response
   *          the response
   * @param relayState
   *          the received relay state
   * @param input
   *          the response processing input
   * @throws ResponseValidationException
   *           for validation errors
   */
  protected void validateRelayState(Response response, String relayState, ResponseProcessingInput input)
      throws ResponseValidationException {

    Optional<String> relayStateOptional = relayState == null || relayState.trim().length() == 0 ? Optional.empty()
        : Optional.of(relayState);
    Optional<String> relayStateInputOptional = input.getRelayState() == null || input.getRelayState().trim().length() == 0
        ? Optional.empty() : Optional.of(input.getRelayState());

    boolean relayStateMatch = (!relayStateOptional.isPresent() && !relayStateInputOptional.isPresent())
        || (relayStateOptional.isPresent() && relayState.equals(input.getRelayState()))
        || (relayStateInputOptional.isPresent() && input.getRelayState().equals(relayState));

    if (!relayStateMatch) {
      String msg = String.format("RelayState variable received with response (%s) does not match the sent one (%s)", relayState, input
        .getRelayState());
      log.error("{} [{}]", msg, logId(response));
      throw new ResponseValidationException(msg);
    }
  }

  /**
   * Validates the assertion.
   * 
   * @param assertion
   *          the assertion to validate
   * @param response
   *          the response that contained the assertion
   * @param input
   *          the processing input
   * @param idpMetadata
   *          the IdP metadat
   * @param validationContext
   *          optional validation context
   * @throws ResponseValidationException
   *           for validation errors
   */
  protected void validateAssertion(Assertion assertion, Response response, ResponseProcessingInput input, EntityDescriptor idpMetadata,
      ValidationContext validationContext) throws ResponseValidationException {

    IDPSSODescriptor descriptor = idpMetadata != null ? idpMetadata.getIDPSSODescriptor(SAMLConstants.SAML20P_NS) : null;
    if (descriptor == null) {
      throw new ResponseValidationException("Invalid/missing IdP metadata - cannot verify Assertion");
    }

    AuthnRequest authnRequest = input.getAuthnRequest();
    String entityID = null;
    if (authnRequest != null) {
      entityID = authnRequest.getIssuer().getValue();
    }

    AssertionValidationParametersBuilder b = AssertionValidationParametersBuilder.builder()
      .strictValidation(this.responseValidationSettings.isStrictValidation())
      .allowedClockSkew(this.responseValidationSettings.getAllowedClockSkew())
      .maxAgeReceivedMessage(this.responseValidationSettings.getMaxAgeResponse())
      .signatureRequired(this.responseValidationSettings.isRequireSignedAssertions())
      .signatureValidationCriteriaSet(new CriteriaSet(new RoleDescriptorCriterion(descriptor), new UsageCriterion(UsageType.SIGNING)))
      .receiveInstant(input.getReceiveInstant())
      .receiveUrl(input.getReceiveURL())
      .authnRequest(authnRequest)
      .expectedIssuer(idpMetadata.getEntityID())
      .responseIssueInstant(response.getIssueInstant().getMillis())
      .validAudiences(entityID)
      .validRecipients(input.getReceiveURL(), entityID);

    if (validationContext != null) {
      b.addStaticParameters(validationContext.getStaticParameters());
      b.addDynamicParameters(validationContext.getDynamicParameters());
    }
    ValidationContext context = b.build();

    ValidationResult result = this.assertionValidator.validate(assertion, context);
    if (validationContext != null) {
      validationContext.getDynamicParameters().putAll(context.getDynamicParameters());
    }
    switch (result) {
    case VALID:
      log.debug("Assertion with ID '{}' was successfully validated", assertion.getID());
      break;
    case INDETERMINATE:
      log.warn("Validation of Assertion with ID '{}' was indeterminate - {}", assertion.getID(), context.getValidationFailureMessage());
      break;
    case INVALID:
      log.error("Validation of Assertion failed - {}", context.getValidationFailureMessage());
      throw new ResponseValidationException(context.getValidationFailureMessage());
    }
  }

  /**
   * Assigns the decrypter instance.
   * 
   * @param decrypter
   *          the decrypter
   */
  public void setDecrypter(SAMLObjectDecrypter decrypter) {
    this.decrypter = decrypter;
  }

  /**
   * Assigns the message replay checker to use.
   * 
   * @param messageReplayChecker
   *          message replay checker
   */
  public void setMessageReplayChecker(MessageReplayChecker messageReplayChecker) {
    this.messageReplayChecker = messageReplayChecker;
  }

  /**
   * Assigns the response validation settings.
   * 
   * @param responseValidationSettings
   *          validation settings
   */
  public void setResponseValidationSettings(ResponseValidationSettings responseValidationSettings) {
    this.responseValidationSettings = responseValidationSettings;
  }

  private static String logId(Response response) {
    return String.format("response-id:'%s'", response.getID() != null ? response.getID() : "<empty>");
  }

  private static String logId(Response response, Assertion assertion) {
    return String.format("response-id:'%s',assertion-id:'%s'",
      response.getID() != null ? response.getID() : "<empty>",
      assertion.getID() != null ? assertion.getID() : "<empty>");
  }

}
