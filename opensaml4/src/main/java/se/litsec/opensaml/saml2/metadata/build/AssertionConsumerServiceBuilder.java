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
package se.litsec.opensaml.saml2.metadata.build;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;

/**
 * A builder for {@code AssertionConsumerService} elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AssertionConsumerServiceBuilder extends AbstractSAMLObjectBuilder<AssertionConsumerService> {

  /**
   * Utility method that creates a builder.
   * 
   * @return a builder
   */
  public static AssertionConsumerServiceBuilder builder() {
    return new AssertionConsumerServiceBuilder();
  }

  /** {@inheritDoc} */
  @Override
  protected Class<AssertionConsumerService> getObjectType() {
    return AssertionConsumerService.class;
  }

  /**
   * Assigns the location URI.
   * 
   * @param location
   *          the URI
   * @return the builder
   */
  public AssertionConsumerServiceBuilder location(String location) {
    this.object().setLocation(location);
    return this;
  }

  /**
   * Assigns the binding of the service
   * 
   * @param binding
   *          the binding URI
   * @return the builder
   * @see #postBinding()
   * @see #redirectBinding()
   */
  public AssertionConsumerServiceBuilder binding(String binding) {
    this.object().setBinding(binding);
    return this;
  }

  /**
   * Shortcut for assigning the SAML POST binding to the service.
   * 
   * @return the builder
   * @see #binding(String)
   */
  public AssertionConsumerServiceBuilder postBinding() {
    this.object().setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
    return this;
  }

  /**
   * Shortcut for assigning the SAML Redirect binding to the service.
   * 
   * @return the builder
   * @see #binding(String)
   */
  public AssertionConsumerServiceBuilder redirectBinding() {
    this.object().setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
    return this;
  }

  /**
   * Assigns the index for the service.
   * 
   * @param index
   *          the index
   * @return the builder
   */
  public AssertionConsumerServiceBuilder index(Integer index) {
    this.object().setIndex(index);
    return this;
  }

  /**
   * Sets the {@code isDefault} attribute of the service.
   * 
   * @param def
   *          the Boolean
   * @return the builder
   */
  public AssertionConsumerServiceBuilder isDefault(Boolean def) {
    this.object().setIsDefault(def);
    return this;
  }

}
