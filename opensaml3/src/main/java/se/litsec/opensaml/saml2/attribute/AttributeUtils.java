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

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.saml2.core.Attribute;

/**
 * Helper methods for accessing attribute values. See also {@link AttributeBuilder}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see AttributeBuilder
 */
public class AttributeUtils {

  /**
   * Given an attribute holding string values this method will return a list of these values.
   * 
   * @param attribute
   *          the attribute
   * @return a (possibly empty) list of string values
   */
  public static List<String> getAttributeStringValues(Attribute attribute) {
    return getAttributeValues(attribute, XSString.class)
      .stream()
      .map(v -> v.getValue())
      .collect(Collectors.toList());
  }

  /**
   * Given a single-valued string attribute, this method returns its string value.
   * 
   * @param attribute
   *          the attribute
   * @return the value, or {@code null} if no value is stored
   */
  public static String getAttributeStringValue(Attribute attribute) {
    XSString v = getAttributeValue(attribute, XSString.class);
    return v != null ? v.getValue() : null;
  }

  /**
   * Returns the attribute values of the given type.
   * 
   * @param attribute
   *          the attribute
   * @param type
   *          the type to match
   * @return a (possibly empty) list of values.
   */
  public static <T extends XMLObject> List<T> getAttributeValues(Attribute attribute, Class<T> type) {
    return attribute.getAttributeValues()
      .stream()
      .filter(a -> type.isInstance(a))
      .map(type::cast)
      .collect(Collectors.toList());
  }

  /**
   * Given a single-valued attribute, this method returns its value (of the given type).
   * 
   * @param attribute
   *          the attribute
   * @param type
   *          the type to match
   * @return the value, or {@code null}
   */
  public static <T extends XMLObject> T getAttributeValue(Attribute attribute, Class<T> type) {
    return attribute.getAttributeValues()
      .stream()
      .filter(a -> type.isInstance(a))
      .map(type::cast)
      .findFirst()
      .orElse(null);
  }

  /**
   * Returns an attribute with a given name from an attribute list.
   * 
   * @param name
   *          the attribute name
   * @param attributes
   *          the list of attributes
   * @return the attribute or {@link Optional#empty()}
   * @see #getAttributes(String, List)
   */
  public static Optional<Attribute> getAttribute(String name, List<Attribute> attributes) {
    if (attributes == null) {
      return Optional.empty();
    }
    return attributes.stream().filter(a -> a.getName().equals(name)).findFirst();
  }

  // Hidden
  private AttributeUtils() {
  }

}
