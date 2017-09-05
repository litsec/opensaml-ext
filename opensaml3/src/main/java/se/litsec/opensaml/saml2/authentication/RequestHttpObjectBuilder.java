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

import org.opensaml.messaging.encoder.MessageEncodingException;
import org.opensaml.saml.saml2.core.RequestAbstractType;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.signature.support.SignatureException;

import net.shibboleth.utilities.java.support.resolver.ResolverException;
import se.litsec.opensaml.core.SAMLObjectBuilder;

/**
 * A generic request builder that is used to create Request messages.
 * <p>
 * A request builder instance may only be used to create one request and should not be re-used. Instead a new builder
 * should be created using a builder factory.
 * </p>
 * <p>
 * By default the request builder creates a Request object based on the SP and IdP settings in metadata and the
 * configuration of the builder factory, but it is also possible to control the request by using chaining calls as
 * illustrated below:
 * </p>
 * 
 * <pre>{@code
 * RequestHttpObject request = builder.relayState("hello").binding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST").build();}
 * </pre>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 *
 * @param <T>
 *          the concrete request type
 */
public interface RequestHttpObjectBuilder<T extends RequestAbstractType> extends SAMLObjectBuilder<T> {

  /**
   * Compiles the request by invoking {@link #build()}, optionally signs it and encodes it according to the configured
   * binding and returns a RequestHttpObject that can be used by the SP application to send the request to the Identity
   * Provider.
   * 
   * @return a RequestHttpObject object
   * @throws SignatureException
   *           for signature creation errors
   * @throws ResolverException
   *           for metadata errors
   * @throws MessageEncodingException
   *           for encoding errors
   */
  RequestHttpObject<T> buildHttpObject() throws SignatureException, ResolverException, MessageEncodingException;

  /**
   * Returns the entityID of the Service Provider that this builder is serving.
   * 
   * @return entityID of the Service Provider
   */
  String entityID();

  /**
   * Returns the entityID for the IdP to which we are constructing the request.
   * 
   * @return entityID of the Identity Provider
   */
  String idpEntityID();

  /**
   * Installs the SAML RelayState to use when sending the request.
   * 
   * @param relayState
   *          the RelayState
   * @return an updated builder object
   */
  RequestHttpObjectBuilder<T> relayState(String relayState);

  /**
   * Returns the SAML RelayState that has been configured for this builder.
   * 
   * @return the SAML RelayState or {@code null} if none has been configured
   */
  String relayState();

  /**
   * Replaces the request object that this builder currently is processing with a new and updated object.
   * <p>
   * Note: Care should be taken when using this method, and if only a particular attribute or element of the request
   * should be modified it is generally better to use the {@link #request()} method that returns a reference to the
   * contained request message, or the special purpose methods for this purpose.
   * </p>
   * 
   * @param request
   *          the request object to install to the builder
   * @return an updated builder object
   * @see #request()
   */
  RequestHttpObjectBuilder<T> request(T request);

  /**
   * Returns a reference to the request object that this builder object is handling. In order to modify parts of the
   * request this method should be used.
   * <p>
   * Also see the methods that directly modifies attributes and elements.
   * </p>
   * 
   * @return a reference to the request object
   */
  T request();

  /**
   * The builder is created with the SAML binding to use when sending the request message (redirect or post). This
   * method may be used to override this setting.
   * 
   * @param binding
   *          the URI of the SAML binding to use (e.g., "urn:oasis:names:tc:SAML:2.0:bindings:HTTP-Redirect")
   * @return an updated builder object
   * @throws ResolverException
   *           if the binding supplied does not match a binding found in the IdP:s entity descriptor
   */
  RequestHttpObjectBuilder<T> binding(String binding) throws ResolverException;

  /**
   * Returns the SAML binding that should be used when sending the request.
   * 
   * @return the URI of the SAML binding to use
   */
  String binding();

  /**
   * The RequestBuilder reads the federation metadata and determines that a request should be signed if based on
   * requirements from the IdP and SP.
   * <p>
   * Using this method it is possible to override the default behaviour by explicitly state the request should be
   * signed, or not signed.
   * </p>
   * 
   * @param signatureFlag
   *          flag telling whether the request being created should be signed or not
   * @return an updated builder object
   */
  RequestHttpObjectBuilder<T> performSignature(boolean signatureFlag);

  /**
   * Predicate that tells whether the request being created will be signed or not.
   * 
   * @return if the request being created will be signed {@code true} is returned, and {@code false} otherwise
   */
  boolean performSignature();

  /**
   * Using this method the signature credentials for the builder object may be changed. This is typically useful when
   * the SP has more than one signature key, or for testing purposes.
   * 
   * @param signatureCredentials
   *          the "new" signature credentials
   * @return an updated builder object
   */
  RequestHttpObjectBuilder<T> signatureCredentials(X509Credential signatureCredentials);

  /**
   * Returns the signature credentials this builder object has been configured to use during request signing.
   * 
   * @return the signature credentials
   */
  X509Credential signatureCredentials();

  /**
   * <b>For testing purposes</b>
   * <p>
   * The method will change the endpoint to where the request will be sent, but will <b>not</b> modify the
   * {@code Destination} attribute of the request element.
   * </p>
   * 
   * @param url
   *          the endpoint to assign
   * @return an updated builder object
   */
  RequestHttpObjectBuilder<T> endpoint(String url);

}
