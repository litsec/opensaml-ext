/*
 * Copyright 2016-2021 Litsec AB
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.schema.XSAny;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSDateTime;
import org.opensaml.core.xml.schema.XSInteger;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.core.xml.util.XMLObjectSupport;
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
  public static List<String> getAttributeStringValues(final Attribute attribute) {
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
   * @return the value, or null if no value is stored
   */
  public static String getAttributeStringValue(final Attribute attribute) {
    return Optional.ofNullable(getAttributeValue(attribute, XSString.class)).map(XSString::getValue).orElse(null);
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
  public static <T extends XMLObject> List<T> getAttributeValues(final Attribute attribute, final Class<T> type) {
    return attribute.getAttributeValues()
      .stream()
      .flatMap(o -> mapAttribute(o, type))
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
  public static <T extends XMLObject> T getAttributeValue(final Attribute attribute, final Class<T> type) {
    return attribute.getAttributeValues()
      .stream()
      .flatMap(o -> mapAttribute(o, type))
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
  public static Optional<Attribute> getAttribute(final String name, final List<Attribute> attributes) {
    if (attributes == null) {
      return Optional.empty();
    }
    return attributes.stream().filter(a -> a.getName().equals(name)).findFirst();
  }

  /**
   * Helper method that filters attribute values based on the requested type.
   * 
   * <p>
   * The SAML core specification section 2.7.3.1.1 states: "If the data content of an {@code <AttributeValue>} element
   * is of an XML Schema simple type (such as xs:integer or xs:string), the datatype MAY be declared explicitly by means
   * of an xsi:type declaration in the {@code <AttributeValue>} element."
   * </p>
   * <p>
   * Therefore this method handles attribute values with no explicit type given for {@code XSString}, {@code XSInteger},
   * {@code XSBoolean} and {@code XSDateTime}.
   * </p>
   * 
   * @param obj
   *          the object being streamed
   * @param type
   *          the requested type
   * @return a stream holding one or zero elements
   */
  private static <T extends XMLObject, R extends XMLObject> Stream<T> mapAttribute(final R obj, final Class<T> type) {
    if (type.isInstance(obj)) {
      return Stream.of(type.cast(obj));
    }
    else if (XSAny.class.isInstance(obj)) {
      if (type.isAssignableFrom(XSString.class)) {
        XSString newObject = (XSString) XMLObjectSupport.buildXMLObject(XSString.TYPE_NAME);
        newObject.setValue(((XSAny) obj).getTextContent());
        return Stream.of(type.cast(newObject));
      }
      else if (type.isAssignableFrom(XSInteger.class)) {
        try {
          Integer v = Integer.parseInt(((XSAny) obj).getTextContent());
          XSInteger newObject = (XSInteger) XMLObjectSupport.buildXMLObject(XSInteger.TYPE_NAME); 
          newObject.setValue(v);
          return Stream.of(type.cast(newObject));
        }
        catch (NumberFormatException e) {
          return Stream.empty();
        }
      }
      else if (type.isAssignableFrom(XSBoolean.class)) {
        String text = ((XSAny) obj).getTextContent();
        if (text == null) {
          return Stream.empty();
        }
        Boolean b = ("true".equalsIgnoreCase(text) || "1".equals(text))
            ? Boolean.TRUE
            : ("false".equalsIgnoreCase(text) || "0".equals(text)) ? Boolean.FALSE : null;
        if (b != null) {
          XSBoolean newObject = (XSBoolean) XMLObjectSupport.buildXMLObject(XSBoolean.TYPE_NAME);               
          XSBooleanValue newValue = XSBooleanValue.valueOf(text);
          newObject.setValue(newValue);
          return Stream.of(type.cast(newObject));
        }
      }
      else if (type.isAssignableFrom(XSDateTime.class)) {
        String text = ((XSAny) obj).getTextContent();
        if (text == null) {
          return Stream.empty();
        }
        try {
          final Instant date = Instant.parse(text);
          if (date != null) {
            XSDateTime newObject = (XSDateTime) XMLObjectSupport.buildXMLObject(XSDateTime.TYPE_NAME);
            newObject.setValue(date);
            return Stream.of(type.cast(newObject));
          }
        }
        catch (Exception e) {
        }
      }
    }
    return Stream.empty();
  }

  // Hidden
  private AttributeUtils() {
  }

}
