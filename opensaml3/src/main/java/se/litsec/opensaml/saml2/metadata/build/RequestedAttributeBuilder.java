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

import org.opensaml.saml.saml2.metadata.RequestedAttribute;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;

/**
 * Builder for {@code md:RequestedAttribute} elements.
 * <p>
 * It is valid to add a value to a requested attribute but this rarely happens so this builder does not support that.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class RequestedAttributeBuilder extends AbstractSAMLObjectBuilder<RequestedAttribute> {

  /**
   * Constructor setting the attribute name.
   * 
   * @param name
   *          the attribute name
   */
  /**
   * Constructor setting the attribute name.
   * 
   * @param name
   *          the attribute name
   */
  public RequestedAttributeBuilder(String name) {
    super();
    if (name == null) {
      throw new IllegalArgumentException("name must not be null");
    }
    this.object().setName(name);
  }

  /**
   * Creates a builder.
   * 
   * @param name
   *          the attribute name
   * @return a builder
   */
  public static RequestedAttributeBuilder builder(String name) {
    return new RequestedAttributeBuilder(name);
  }

  /**
   * Assigns the attribute friendly name.
   * 
   * @param friendlyName
   *          the friendly name
   * @return the builder
   */
  public RequestedAttributeBuilder friendlyName(String friendlyName) {
    this.object().setFriendlyName(friendlyName);
    return this;
  }

  /**
   * Assigns the attribute name format.
   * 
   * @param nameFormat
   *          the name format URI
   * @return the builder
   */
  public RequestedAttributeBuilder nameFormat(String nameFormat) {
    this.object().setNameFormat(nameFormat);
    return this;
  }

  /**
   * Assigns the {@code isRequired} attribute value.
   * 
   * @param required
   *          flag
   * @return the builder
   */
  public RequestedAttributeBuilder isRequired(Boolean required) {
    this.object().setIsRequired(required);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  protected Class<RequestedAttribute> getObjectType() {
    return RequestedAttribute.class;
  }

}
