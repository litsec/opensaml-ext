/*
 * Copyright 2016-2019 Litsec AB
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.binding.SAMLBindingSupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPPostEncoder;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.codec.Base64Support;
import net.shibboleth.utilities.java.support.codec.HTMLEncoder;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import se.litsec.opensaml.utils.SignatureUtils;

/**
 * A RequestHttpObject for sending using HTTP POST.
 * <p>
 * If signature credentials are supplied when creating the object the request will be signed.
 * </p>
 *
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 *
 * @param <T>
 *          the type of the request
 */
public class PostRequestHttpObject<T extends RequestAbstractType> extends HTTPPostEncoder implements RequestHttpObject<T> {

  /** Logging instance. */
  private static final Logger logger = LoggerFactory.getLogger(PostRequestHttpObject.class);

  /** The request. */
  private T request;

  /** The URL to redirect to. */
  private String sendUrl;

  /** HTTP headers. */
  private Map<String, String> httpHeaders = new HashMap<>();

  /** The POST parameters. */
  private Map<String, String> postParameters = new HashMap<>();

  /**
   * Constructor that puts together the resulting object.
   *
   * @param request
   *          the request object
   * @param relayState
   *          the relay state
   * @param signatureCredentials
   *          optional signature credentials
   * @param endpoint
   *          the endpoint where we send this request to
   * @throws MessageEncodingException
   *           for encoding errors
   * @throws SignatureException
   *           for signature errors
   * @deprecated Use
   *             {@link #PostRequestHttpObject(RequestAbstractType, String, X509Credential, String, EntityDescriptor)}
   *             or
   *             {@link #PostRequestHttpObject(RequestAbstractType, String, X509Credential, String, EntityDescriptor, SignatureSigningConfiguration)}
   *             instead
   */
  @Deprecated
  public PostRequestHttpObject(T request, String relayState, X509Credential signatureCredentials, String endpoint)
      throws MessageEncodingException, SignatureException {
    this(request, relayState, signatureCredentials, endpoint, null);
  }

  /**
   * Constructor that puts together the resulting object.
   *
   * @param request
   *          the request object
   * @param relayState
   *          the relay state
   * @param signatureCredentials
   *          optional signature credentials
   * @param endpoint
   *          the endpoint where we send this request to
   * @param recipientMetadata
   *          the recipient metadata (may be {@code null})
   * @throws MessageEncodingException
   *           for encoding errors
   * @throws SignatureException
   *           for signature errors
   */
  public PostRequestHttpObject(T request, String relayState, X509Credential signatureCredentials,
      String endpoint, EntityDescriptor recipientMetadata) throws MessageEncodingException, SignatureException {
    this(request, relayState, signatureCredentials, endpoint, recipientMetadata, null);
  }

  /**
   * Constructor that puts together the resulting object.
   *
   * @param request
   *          the request object
   * @param relayState
   *          the relay state
   * @param signatureCredentials
   *          optional signature credentials
   * @param endpoint
   *          the endpoint where we send this request to
   * @param recipientMetadata
   *          the recipient metadata (may be {@code null})
   * @param defaultSignatureSigningConfiguration
   *          the default signature configuration for the application. If {@code null}, the value returned from
   *          {@link SecurityConfigurationSupport#getGlobalSignatureSigningConfiguration()} will be used
   * @throws MessageEncodingException
   *           for encoding errors
   * @throws SignatureException
   *           for signature errors
   */
  public PostRequestHttpObject(T request, String relayState, X509Credential signatureCredentials,
      String endpoint, EntityDescriptor recipientMetadata, SignatureSigningConfiguration defaultSignatureSigningConfiguration)
      throws MessageEncodingException, SignatureException {

    this.request = request;

    MessageContext<T> context = new MessageContext<>();
    context.setMessage(request);

    // Assign endpoint (sendUrl)
    //
    this.sendUrl = endpoint;

    // Assign SAMLRequest
    //
    if (signatureCredentials != null) {
      logger.trace("Signing SAML Request message ...");
      SignatureUtils.sign(this.request, signatureCredentials,
        defaultSignatureSigningConfiguration != null
            ? defaultSignatureSigningConfiguration
            : SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration(),
        recipientMetadata);
    }

    logger.trace("Marshalling and Base64 encoding SAML message");
    Element domMessage = this.marshallMessage(context.getMessage());

    try {
      String messageXML = SerializeSupport.nodeToString(domMessage);
      String encodedMessage = Base64Support.encode(messageXML.getBytes("UTF-8"), Base64Support.UNCHUNKED);
      this.postParameters.put("SAMLRequest", encodedMessage);
    }
    catch (UnsupportedEncodingException e) {
      logger.error("UTF-8 encoding is not supported, this VM is not Java compliant.");
      throw new MessageEncodingException("Unable to encode message, UTF-8 encoding is not supported");
    }

    // Assign RelayState
    //
    if (SAMLBindingSupport.checkRelayState(relayState)) {
      String encodedRelayState = HTMLEncoder.encodeForHTMLAttribute(relayState);
      logger.debug("Setting RelayState parameter to: '{}', encoded as '{}'", relayState, encodedRelayState);
      this.postParameters.put("RelayState", encodedRelayState);
    }

    // HTTP headers
    //
    this.httpHeaders.put("Cache-control", "no-cache, no-store");
    this.httpHeaders.put("Pragma", "no-cache");
  }

  /** {@inheritDoc} */
  @Override
  public String getSendUrl() {
    return this.sendUrl;
  }

  /** {@inheritDoc} */
  @Override
  public String getMethod() {
    return SAMLConstants.POST_METHOD;
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, String> getRequestParameters() {
    return this.postParameters;
  }

  /** {@inheritDoc} */
  @Override
  public Map<String, String> getHttpHeaders() {
    return this.httpHeaders;
  }

  /** {@inheritDoc} */
  @Override
  public T getRequest() {
    return this.request;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return String.format("request-type='%s', sendUrl='%s', httpHeaders='%s, postParameters=%s", this.request.getClass().getSimpleName(),
      this.sendUrl, this.httpHeaders, this.postParameters);
  }

}
