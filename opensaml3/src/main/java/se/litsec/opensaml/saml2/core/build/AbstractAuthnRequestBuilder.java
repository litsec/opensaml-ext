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

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;

/**
 * Abstract builder for {@code AuthnRequest} messages.
 */
public abstract class AbstractAuthnRequestBuilder<BUILDER extends AbstractSAMLObjectBuilder<AuthnRequest>> extends AbstractRequestBuilder<AuthnRequest, BUILDER> {

  /** {@inheritDoc} */
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
  public BUILDER forceAuthn(Boolean b) {
    this.object().setForceAuthn(b);
    return this.getThis();
  }

  /**
   * Assigns the {@code IsPassive} attribute to the {@code AuthnRequest} object.
   * 
   * @param b
   *          boolean flag
   * @return the builder
   */
  public BUILDER isPassive(Boolean b) {
    this.object().setIsPassive(b);
    return this.getThis();
  }

  /**
   * Assigns the {@code ProtocolBinding} attribute to the {@code AuthnRequest} object.
   * 
   * @param binding
   *          the binding URI
   * @return the builder
   * @see #postProtocolBinding()
   */
  public BUILDER protocolBinding(String binding) {
    this.object().setProtocolBinding(binding);
    return this.getThis();
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
  public BUILDER postProtocolBinding() {
    this.object().setProtocolBinding(SAMLConstants.SAML2_POST_BINDING_URI);
    return this.getThis();
  }

  /**
   * Assigns the {@code AssertionConsumerServiceIndex} attribute to the {@code AuthnRequest} object.
   * 
   * @param index
   *          the index
   * @return the builder
   */
  public BUILDER assertionConsumerServiceIndex(Integer index) {
    this.object().setAssertionConsumerServiceIndex(index);
    return this.getThis();
  }

  /**
   * Assigns the {@code AssertionConsumerServiceURL} attribute to the {@code AuthnRequest} object.
   * 
   * @param url
   *          the URL
   * @return the builder
   */
  public BUILDER assertionConsumerServiceURL(String url) {
    this.object().setAssertionConsumerServiceURL(url);
    return this.getThis();
  }

  /**
   * Assigns the {@code AttributeConsumerServiceIndex} attribute to the {@code AuthnRequest} object.
   * 
   * @param index
   *          the index
   * @return the builder
   */
  public BUILDER attributeConsumerServiceIndex(Integer index) {
    this.object().setAttributeConsumingServiceIndex(index);
    return this.getThis();
  }

  /**
   * Assigns the {@code ProviderName} attribute to the {@code AuthnRequest} object.
   * 
   * @param name
   *          the provider name
   * @return the builder
   */
  public BUILDER providerName(String name) {
    this.object().setProviderName(name);
    return this.getThis();
  }

  /**
   * Assigns a {@code Subject} element to the {@code AuthnRequest} object.
   * 
   * @param subject
   *          the subject (will be cloned before assignment)
   * @return the builder
   */
  public BUILDER subject(Subject subject) {
    try {
      this.object().setSubject(XMLObjectSupport.cloneXMLObject(subject));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this.getThis();
  }

  /**
   * Assigns a {@code NameIDPolicy} element to the {@code AuthnRequest} object.
   * 
   * @param nameIDPolicy
   *          the nameID policy (will be cloned before assignment)
   * @return the builder
   * @see NameIDPolicyBuilder
   */
  public BUILDER nameIDPolicy(NameIDPolicy nameIDPolicy) {
    try {
      this.object().setNameIDPolicy(XMLObjectSupport.cloneXMLObject(nameIDPolicy));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this.getThis();
  }

  /**
   * Assigns a {@code Conditions} element to the {@code AuthnRequest} object.
   * 
   * @param conditions
   *          the request conditions (will be cloned before assignment)
   * @return the builder
   */
  public BUILDER conditions(Conditions conditions) {
    try {
      this.object().setConditions(XMLObjectSupport.cloneXMLObject(conditions));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this.getThis();
  }

  /**
   * Assigns a {@code RequestedAuthnContext} element to the {@code AuthnRequest} object.
   * 
   * @param requestedAuthnContext
   *          the requested authentication context (will be cloned before assignment)
   * @return the builder
   * @see RequestedAuthnContextBuilder
   */
  public BUILDER requestedAuthnContext(RequestedAuthnContext requestedAuthnContext) {
    try {
      this.object().setRequestedAuthnContext(XMLObjectSupport.cloneXMLObject(requestedAuthnContext));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this.getThis();
  }

  /**
   * Assigns a {@code Scoping} element to the {@code AuthnRequest} object.
   * 
   * @param scoping
   *          the scoping element to add (will be cloned before assignment)
   * @return the builder
   */
  public BUILDER scoping(Scoping scoping) {
    try {
      this.object().setScoping(XMLObjectSupport.cloneXMLObject(scoping));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this.getThis();
  }

  /** {@inheritDoc} */
  @Override
  protected Class<AuthnRequest> getObjectType() {
    return AuthnRequest.class;
  }

}
