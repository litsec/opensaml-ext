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
package se.litsec.opensaml.saml2.core.build;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.Conditions;
import org.opensaml.saml.saml2.core.NameIDPolicy;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml.saml2.core.Scoping;
import org.opensaml.saml.saml2.core.Subject;

/**
 * Builder for {@code AuthnRequest} messages.
 */
public class AuthnRequestBuilder extends AbstractRequestBuilder<AuthnRequest, AuthnRequestBuilder> {

  /**
   * Utility method that creates a builder.
   * 
   * @return a builder
   */
  public static AuthnRequestBuilder builder() {
    return new AuthnRequestBuilder();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AuthnRequest build() {
    if (this.object().getProtocolBinding() == null) {
      this.postProtocolBinding();
    }
    return super.build();
  }

  /**
   * Assigns the {@code ForceAuthn} attribute to the {@code AuthnRequest} object.
   * 
   * @param b
   *          boolean flag
   * @return the builder
   */
  public AuthnRequestBuilder forceAuthn(Boolean b) {
    this.object().setForceAuthn(b);
    return this;
  }

  /**
   * Assigns the {@code IsPassive} attribute to the {@code AuthnRequest} object.
   * 
   * @param b
   *          boolean flag
   * @return the builder
   */
  public AuthnRequestBuilder isPassive(Boolean b) {
    this.object().setIsPassive(b);
    return this;
  }

  /**
   * Assigns the {@code ProtocolBinding} attribute to the {@code AuthnRequest} object.
   * 
   * @param binding
   *          the binding URI
   * @return the builder
   * @see #postProtocolBinding()
   */
  public AuthnRequestBuilder protocolBinding(String binding) {
    this.object().setProtocolBinding(binding);
    return this;
  }

  /**
   * Assigns {@link SAMLConstants#SAML2_POST_BINDING_URI} to the {@code ProtocolBinding} attribute of the
   * {@code AuthnRequest} object.
   * 
   * <p>
   * This is the default.
   * </p>
   * 
   * @return the builder
   */
  public AuthnRequestBuilder postProtocolBinding() {
    this.object().setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);
    return this;
  }

  /**
   * Assigns the {@code AssertionConsumerServiceIndex} attribute to the {@code AuthnRequest} object.
   * 
   * @param index
   *          the index
   * @return the builder
   */
  public AuthnRequestBuilder assertionConsumerServiceIndex(Integer index) {
    this.object().setAssertionConsumerServiceIndex(index);
    return this;
  }

  /**
   * Assigns the {@code AssertionConsumerServiceURL} attribute to the {@code AuthnRequest} object.
   * 
   * @param url
   *          the URL
   * @return the builder
   */
  public AuthnRequestBuilder assertionConsumerServiceURL(String url) {
    this.object().setAssertionConsumerServiceURL(url);
    return this;
  }

  /**
   * Assigns the {@code AttributeConsumerServiceIndex} attribute to the {@code AuthnRequest} object.
   * 
   * @param index
   *          the index
   * @return the builder
   */
  public AuthnRequestBuilder attributeConsumerServiceIndex(Integer index) {
    this.object().setAttributeConsumingServiceIndex(index);
    return this;
  }

  /**
   * Assigns the {@code ProviderName} attribute to the {@code AuthnRequest} object.
   * 
   * @param name
   *          the provider name
   * @return the builder
   */
  public AuthnRequestBuilder providerName(String name) {
    this.object().setProviderName(name);
    return this;
  }

  /**
   * Assigns a {@code Subject} element to the {@code AuthnRequest} object.
   * 
   * @param subject
   *          the subject (will be cloned before assignment)
   * @return the builder
   */
  public AuthnRequestBuilder subject(Subject subject) {
    try {
      this.object().setSubject(XMLObjectSupport.cloneXMLObject(subject));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  /**
   * Assigns a {@code NameIDPolicy} element to the {@code AuthnRequest} object.
   * 
   * @param nameIDPolicy
   *          the nameID policy (will be cloned before assignment)
   * @return the builder
   * @see NameIDPolicyBuilder
   */
  public AuthnRequestBuilder nameIDPolicy(NameIDPolicy nameIDPolicy) {
    try {
      this.object().setNameIDPolicy(XMLObjectSupport.cloneXMLObject(nameIDPolicy));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  /**
   * Assigns a {@code Conditions} element to the {@code AuthnRequest} object.
   * 
   * @param conditions
   *          the request conditions (will be cloned before assignment)
   * @return the builder
   */
  public AuthnRequestBuilder conditions(Conditions conditions) {
    try {
      this.object().setConditions(XMLObjectSupport.cloneXMLObject(conditions));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  /**
   * Assigns a {@code RequestedAuthnContext} element to the {@code AuthnRequest} object.
   * 
   * @param requestedAuthnContext
   *          the requested authentication context (will be cloned before assignment)
   * @return the builder
   * @see RequestedAuthnContextBuilder
   */
  public AuthnRequestBuilder requestedAuthnContext(RequestedAuthnContext requestedAuthnContext) {
    try {
      this.object().setRequestedAuthnContext(XMLObjectSupport.cloneXMLObject(requestedAuthnContext));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  /**
   * Assigns a {@code Scoping} element to the {@code AuthnRequest} object.
   * 
   * @param scoping
   *          the scoping element to add (will be cloned before assignment)
   * @return the builder
   */
  public AuthnRequestBuilder scoping(Scoping scoping) {
    try {
      this.object().setScoping(XMLObjectSupport.cloneXMLObject(scoping));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  /** {@inheritDoc} */
  @Override
  protected Class<AuthnRequest> getObjectType() {
    return AuthnRequest.class;
  }

  /** {@inheritDoc} */
  @Override
  protected AuthnRequestBuilder getThis() {
    return this;
  }

}
