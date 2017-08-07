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
package se.litsec.opensaml.saml2.authentication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.opensaml.messaging.context.MessageContext;
import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.common.messaging.context.SAMLBindingContext;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.binding.encoding.impl.HTTPRedirectDeflateEncoder;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.saml.saml2.metadata.Endpoint;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.context.SecurityParametersContext;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.impl.BasicSignatureSigningParametersResolver;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * A RequestHttpObject for sending using HTTP GET (redirect binding).
 * <p>
 * If signature credentials are supplied when creating the object the request will be signed.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 *
 * @param <T>
 *          the type of the request
 */
public class RedirectRequestHttpObject<T extends RequestAbstractType> extends HTTPRedirectDeflateEncoder implements RequestHttpObject<T> {

  /** Logging instance. */
  private static final Logger logger = LoggerFactory.getLogger(RedirectRequestHttpObject.class);

  /** The request. */
  private T request;

  /** The URL to redirect to. */
  private String sendUrl;

  /** HTTP headers. */
  private Map<String, String> httpHeaders = new HashMap<String, String>();

  /**
   * Constructor that puts together to resulting object.
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
   */
  public RedirectRequestHttpObject(T request, String relayState, X509Credential signatureCredentials, String endpoint)
      throws MessageEncodingException, SignatureException {

    this.request = request;

    // Set up the message context.
    //
    MessageContext<SAMLObject> messageContext = new MessageContext<SAMLObject>();
    messageContext.setMessage(request);
    messageContext.getSubcontext(SAMLBindingContext.class, true).setRelayState(relayState);
    Endpoint endpointObject = ObjectUtils.createSamlObject(Endpoint.class);
    endpointObject.setBinding(this.getBindingURI());
    endpointObject.setLocation(endpoint);

    if (signatureCredentials != null) {
      BasicSignatureSigningConfiguration signatureCreds = new BasicSignatureSigningConfiguration();
      signatureCreds.setSigningCredentials(Arrays.asList(signatureCredentials));

      BasicSignatureSigningParametersResolver signatureParametersResolver = new BasicSignatureSigningParametersResolver();
      CriteriaSet criteriaSet = new CriteriaSet(new SignatureSigningConfigurationCriterion(SecurityConfigurationSupport
        .getGlobalSignatureSigningConfiguration(), signatureCreds));

      try {
        SignatureSigningParameters parameters = signatureParametersResolver.resolveSingle(criteriaSet);
        messageContext.getSubcontext(SecurityParametersContext.class, true).setSignatureSigningParameters(parameters);
      }
      catch (ResolverException e) {
        throw new SignatureException(e);
      }
    }

    // Put together the message.
    //
    this.removeSignature(this.request);
    String encodedMessage = this.deflateAndBase64Encode(this.request);
    this.sendUrl = this.buildRedirectURL(messageContext, endpoint, encodedMessage);
    logger.trace("Redirect URL is {}", this.sendUrl);

    this.httpHeaders.put("Cache-control", "no-cache, no-store");
    this.httpHeaders.put("Pragma", "no-cache");
    // TODO: UTF-8 character encoding
  }

  /** {@inheritDoc} */
  @Override
  public String getSendUrl() {
    return this.sendUrl;
  }

  /** {@inheritDoc} */
  @Override
  public String getMethod() {
    return SAMLConstants.GET_METHOD;
  }

  /**
   * Will always return {@code null}.
   */
  @Override
  public Map<String, String> getRequestParameters() {
    return null;
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
    return String.format("request-type='%s', sendUrl='%s', httpHeaders=%s", this.request.getClass().getSimpleName(), this.sendUrl,
      this.httpHeaders);
  }

}
