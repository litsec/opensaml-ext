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
package se.litsec.opensaml.saml2.attribute;

import org.opensaml.saml.saml2.core.Attribute;

/**
 * An attribute template is a template of a SAML attribute, i.e., it represents the name, friendly name and name format
 * but not the value of the attribute.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AttributeTemplate {

  /** The attribute name. */
  private String name;

  /** The attribute friendly name. */
  private String friendlyName;

  /**
   * The name format of this attribute. The default is {@code urn:oasis:names:tc:SAML:2.0:attrname-format:uri} (
   * {@link Attribute#URI_REFERENCE}).
   */
  private String nameFormat = Attribute.URI_REFERENCE;

  /**
   * Creates an attribute template with the given name and friendly name, the default name format
   * {@code urn:oasis:names:tc:SAML:2.0:attrname-format:uri} ({@link Attribute#URI_REFERENCE}) and not multi-valued.
   * 
   * @param name
   *          the attribute name
   * @param friendlyName
   *          the attribute friendly name (optional)
   */
  public AttributeTemplate(String name, String friendlyName) {
    this(name, friendlyName, Attribute.URI_REFERENCE);
  }

  /**
   * Creates an attribute template with the given name, friendly name and name format.
   * 
   * @param name
   *          the attribute name
   * @param friendlyName
   *          the attribute friendly name
   * @param nameFormat
   *          the name format
   * @param multiValued
   *          indicates whether this attribute may have more than one value
   */
  public AttributeTemplate(String name, String friendlyName, String nameFormat) {
    if (name == null) {
      throw new NullPointerException("'name' must not be null");
    }
    this.name = name;
    this.friendlyName = friendlyName;
    this.nameFormat = nameFormat != null ? nameFormat : Attribute.URI_REFERENCE;
  }

  /**
   * Get the name of this attribute template.
   * 
   * @return the name of this attribute template
   */
  public String getName() {
    return this.name;
  }

  /**
   * Get the friendly name of this attribute template.
   * 
   * @return the friendly name of this attribute template
   */
  public String getFriendlyName() {
    return this.friendlyName;
  }

  /**
   * Get the name format of this attribute template.
   * 
   * @return the name format of this attribute template
   */
  public String getNameFormat() {
    return this.nameFormat;
  }

  /**
   * Based on the attribute template an {@link AttributeBuilder} object is created.
   * 
   * @return a builder
   */
  public AttributeBuilder createBuilder() {
    AttributeBuilder builder = new AttributeBuilder(this.name);
    return builder.friendlyName(this.friendlyName).nameFormat(this.nameFormat);
  }

}
