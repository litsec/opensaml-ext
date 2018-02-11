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
      .map(XSString::getValue)
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
   * @param <T>
   *          the value type
   * @return a (possibly empty) list of values.
   */
  public static <T extends XMLObject> List<T> getAttributeValues(Attribute attribute, Class<T> type) {
    return attribute.getAttributeValues()
      .stream()
      .filter(type::isInstance)
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
   * @param <T>
   *          the value type
   * @return the value, or {@code null}
   */
  public static <T extends XMLObject> T getAttributeValue(Attribute attribute, Class<T> type) {
    return attribute.getAttributeValues()
      .stream()
      .filter(type::isInstance)
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
