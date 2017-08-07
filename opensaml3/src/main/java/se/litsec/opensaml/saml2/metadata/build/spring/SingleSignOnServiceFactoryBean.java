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

import org.opensaml.saml.saml2.metadata.SingleSignOnService;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.SingleSignOnServiceBuilder;

/**
 * A Spring factory bean for creating {@link SingleSignOnService} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see SingleSignOnServiceBuilder
 */
public class SingleSignOnServiceFactoryBean extends AbstractSAMLObjectBuilderFactoryBean<SingleSignOnService> {

  /** The builder. */
  private SingleSignOnServiceBuilder builder;

  /**
   * Default constructor.
   */
  public SingleSignOnServiceFactoryBean() {
    this.builder = SingleSignOnServiceBuilder.builder();
  }

  /**
   * Assigns the binding URI.
   * 
   * @param binding
   *          the binding URI
   */
  public void setBinding(String binding) {
    this.builder.binding(binding);
  }

  /**
   * Assigns the location URL.
   * 
   * @param location
   *          the location URL
   */
  public void setLocation(String location) {
    this.builder.location(location);
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<SingleSignOnService> builder() {
    return this.builder;
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return SingleSignOnService.class;
  }

}
