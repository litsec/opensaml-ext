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
package se.litsec.opensaml.saml2.metadata.build;

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


  /** {@inheritDoc} */
  @Override
  protected Class<SingleSignOnService> getObjectType() {
    return SingleSignOnService.class;
  }

}
