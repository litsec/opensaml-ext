/*
 * The opensaml-ext project is an open-source package that extends OpenSAML
 * with useful extensions and utilities.
 *
 * More details on <https://github.com/litsec/opensaml-ext>
 * Copyright (C) 2017 Litsec AB
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
    RequestGenerator<T, I> {

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
        return new RedirectRequestHttpObject<T>(request, input.getRelayState(), signCred, destination);
      }
      else if (SAMLConstants.SAML2_POST_BINDING_URI.equals(binding)) {
        // POST binding
        return new PostRequestHttpObject<T>(request, input.getRelayState(), signCred, destination);
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
   * Assigns the signature credentials for the SP
   * 
   * @param signingCredentials
   *          signature credentials
   */
  public void setSigningCredentials(X509Credential signingCredentials) {
    this.signingCredentials = signingCredentials;
  }

}
