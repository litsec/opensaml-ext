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
package se.litsec.opensaml.saml2.common.request;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import se.litsec.opensaml.saml2.metadata.PeerMetadataResolver;

/**
 * Abstract base class for request generators.
 *
 * @param <T>
 *          the request type
 * @param <I>
 *          the type of the input required by this generator
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public abstract class AbstractRequestGenerator<T extends RequestAbstractType, I extends RequestGeneratorInput> implements
    RequestGenerator<T, I>, InitializingBean {

  /** Logging instance. */
  private final Logger log = LoggerFactory.getLogger(AbstractRequestGenerator.class);

  /** The SP entityID. */
  private String entityID;

  /** The name of the SP (for display). */
  private String name;

  /** The signature credentials for the SP. */
  private X509Credential signingCredentials;

  /** Random generator. */
  private Random randomizer = new SecureRandom(String.valueOf(System.currentTimeMillis()).getBytes());

  /**
   * Constructor assigning the Service Provider entityID.
   * 
   * @param entityID
   *          the entityID
   */
  public AbstractRequestGenerator(String entityID) {
    this.entityID = entityID;
    if (this.entityID == null || this.entityID.trim().isEmpty()) {
      throw new IllegalArgumentException("entityID must not be null or empty");
    }
  }

  /**
   * Returns the peer metadata.
   * 
   * @param input
   *          the request generation input
   * @param metadataResolver
   *          the metadata resolver
   * @return peer metadata
   * @throws RequestGenerationException
   *           if no metadata is found
   */
  protected EntityDescriptor getPeerMetadata(RequestGeneratorInput input, PeerMetadataResolver metadataResolver)
      throws RequestGenerationException {
    EntityDescriptor metadata = metadataResolver.getMetadata(input.getPeerEntityID());
    if (metadata == null) {
      throw new RequestGenerationException("No metadata available for " + input.getPeerEntityID());
    }
    return metadata;
  }

  /**
   * Builds a request HTTP object (including signing).
   * 
   * @param request
   *          the actual request
   * @param input
   *          the request generation input
   * @param binding
   *          the binding to use
   * @param destination
   *          the destination URL
   * @return a request HTTP object
   * @throws RequestGenerationException
   *           for errors during signing or encoding
   */
  protected RequestHttpObject<T> buildRequestHttpObject(T request, I input, String binding, String destination)
      throws RequestGenerationException {

    X509Credential signCred = input.getOverrideSigningCredential();
    if (signCred == null) {
      signCred = this.getSigningCredentials();
    }

    try {
      if (SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(binding)) {
        // Redirect binding
        return new RedirectRequestHttpObject<>(request, input.getRelayState(), signCred, destination);
      }
      else if (SAMLConstants.SAML2_POST_BINDING_URI.equals(binding)) {
        // POST binding
        return new PostRequestHttpObject<>(request, input.getRelayState(), signCred, destination);
      }
      else {
        throw new RequestGenerationException("Unsupported binding: " + binding);
      }
    }
    catch (MessageEncodingException | SignatureException e) {
      String msg = "Failed to encode/sign request for transport";
      log.error(msg, e);
      throw new RequestGenerationException(msg);
    }
  }

  /**
   * Generates a request ID.
   * 
   * @return request ID
   */
  protected String generateID() {
    return "_" + new BigInteger(128, this.randomizer).toString(16);
  }

  /**
   * Returns the signature credentials.
   * 
   * @return the signature credentials
   */
  protected X509Credential getSigningCredentials() {
    return this.signingCredentials;
  }

  /**
   * Returns the randomizer for generation of ID:s etc.
   * 
   * @return randomizer instance
   */
  protected Random getRandomizer() {
    return this.randomizer;
  }

  /**
   * Returns the binding URI for the binding the generator should use if there are more than one possible choice. May be
   * overridden by {@link RequestGeneratorInput#getPreferredBinding()}.
   * 
   * @return binding URI
   */
  protected abstract String getDefaultBinding();

  /** {@inheritDoc} */
  @Override
  public String getEntityID() {
    return this.entityID;
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return this.name;
  }

  /**
   * The human readable name for the SP instance
   * 
   * @param name
   *          the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Assigns the signature credentials for the SP. If not assigned, signing will not be possible.
   * 
   * @param signingCredentials
   *          signature credentials
   */
  public void setSigningCredentials(X509Credential signingCredentials) {
    this.signingCredentials = signingCredentials;
  }

  /** {@inheritDoc} */
  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.hasText(this.name, "Property 'name' must be assigned");
    if (this.signingCredentials == null) {
      log.warn("No signature credentials assigned - signing will not be possible");
    }
  }

}
