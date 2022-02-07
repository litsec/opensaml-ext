/*
 * Copyright 2016-2022 Litsec AB
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

import javax.xml.namespace.QName;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;

/**
 * A builder for {@code SingleSignOnService} elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SingleSignOnServiceBuilder extends AbstractSAMLObjectBuilder<SingleSignOnService> {
  
  /**
   * Utility method that creates a builder.
   * 
   * @return a builder
   */
  public static SingleSignOnServiceBuilder builder() {
    return new SingleSignOnServiceBuilder();
  }

  /**
   * Assigns the location URI.
   * 
   * @param location
   *          the URI
   * @return the builder
   */
  public SingleSignOnServiceBuilder location(String location) {
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
  public SingleSignOnServiceBuilder binding(String binding) {
    this.object().setBinding(binding);
    return this;
  }

  /**
   * Shortcut for assigning the SAML POST binding to the service.
   * 
   * @return the builder
   * @see #binding(String)
   */
  public SingleSignOnServiceBuilder postBinding() {
    this.object().setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
    return this;
  }

  /**
   * Shortcut for assigning the SAML Redirect binding to the service.
   * 
   * @return the builder
   * @see #binding(String)
   */
  public SingleSignOnServiceBuilder redirectBinding() {
    this.object().setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
    return this;
  }

  public SingleSignOnServiceBuilder protocolBinding(final String binding) {
    this.object()
      .getUnknownAttributes()
      .put(new QName("urn:oasis:names:tc:SAML:2.0:profiles:holder-of-key:SSO:browser", "ProtocolBinding", "hoksso"), binding);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  protected Class<SingleSignOnService> getObjectType() {
    return SingleSignOnService.class;
  }

}
