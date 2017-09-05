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
package se.litsec.opensaml.saml2.metadata.build.spring;

import org.opensaml.saml.saml2.metadata.AssertionConsumerService;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.AssertionConsumerServiceBuilder;

/**
 * A Spring factory bean for creating {@link AssertionConsumerService} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AssertionConsumerServiceFactoryBean extends AbstractSAMLObjectBuilderFactoryBean<AssertionConsumerService> {

  /** The builder. */
  private AssertionConsumerServiceBuilder builder;

  /**
   * Constructor.
   */
  public AssertionConsumerServiceFactoryBean() {
    this.builder = new AssertionConsumerServiceBuilder();
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return AssertionConsumerService.class;
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<AssertionConsumerService> builder() {
    return this.builder;
  }

  /**
   * Assigns the location URI.
   * 
   * @param location
   *          the URI
   * @see AssertionConsumerServiceBuilder#location(String)
   */
  public void setLocation(String location) {
    this.builder.location(location);
  }

  /**
   * Assigns the binding of the service
   * 
   * @param binding
   *          the binding URI
   * @see AssertionConsumerServiceBuilder#binding(String)
   */
  public void setBinding(String binding) {
    this.builder.binding(binding);
  }

  /**
   * Assigns the index for the service.
   * 
   * @param index
   *          the index
   * @see AssertionConsumerServiceBuilder#index(Integer)
   */
  public void setIndex(Integer index) {
    this.builder.index(index);
  }

  /**
   * Sets the {@code isDefault} attribute of the service.
   * 
   * @param isDefault
   *          the Boolean
   * @see AssertionConsumerServiceBuilder#isDefault(Boolean)
   */
  public void setIsDefault(Boolean isDefault) {
    this.builder.isDefault(isDefault);
  }

}
