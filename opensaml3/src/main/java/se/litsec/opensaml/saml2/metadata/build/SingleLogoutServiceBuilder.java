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
import org.opensaml.saml.saml2.metadata.SingleLogoutService;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;

/**
 * A builder for {@code SingleLogoutService} elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SingleLogoutServiceBuilder extends AbstractSAMLObjectBuilder<SingleLogoutService> {
  
  /**
   * Utility method that creates a builder.
   * 
   * @return a builder
   */
  public static SingleLogoutServiceBuilder builder() {
    return new SingleLogoutServiceBuilder();
  }

  /**
   * Assigns the location URI.
   * 
   * @param location
   *          the URI
   * @return the builder
   */
  public SingleLogoutServiceBuilder location(String location) {
    this.object().setLocation(location);
    return this;
  }
  
  /**
   * Assigns the response location URI.
   * 
   * @param responseLocation
   *          the URI
   * @return the builder
   */
  public SingleLogoutServiceBuilder responseLocation(String responseLocation) {
    this.object().setResponseLocation(responseLocation);
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
  public SingleLogoutServiceBuilder binding(String binding) {
    this.object().setBinding(binding);
    return this;
  }

  /**
   * Shortcut for assigning the SAML POST binding to the service.
   * 
   * @return the builder
   * @see #binding(String)
   */
  public SingleLogoutServiceBuilder postBinding() {
    this.object().setBinding(SAMLConstants.SAML2_POST_BINDING_URI);
    return this;
  }

  /**
   * Shortcut for assigning the SAML Redirect binding to the service.
   * 
   * @return the builder
   * @see #binding(String)
   */
  public SingleLogoutServiceBuilder redirectBinding() {
    this.object().setBinding(SAMLConstants.SAML2_REDIRECT_BINDING_URI);
    return this;
  }


  /** {@inheritDoc} */
  @Override
  protected Class<SingleLogoutService> getObjectType() {
    return SingleLogoutService.class;
  }

}
