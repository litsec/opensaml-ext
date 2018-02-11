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

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeValue;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLRuntimeException;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.schema.XSString;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Implements the build pattern to create {@link Attribute} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AttributeBuilder extends AbstractSAMLObjectBuilder<Attribute> {

  /** The default name format for the attribute being built. */
  public static final String DEFAULT_NAME_FORMAT = Attribute.URI_REFERENCE;

  /**
   * Constructor setting the attribute name.
   * 
   * @param name
   *          the attribute name
   */
  public AttributeBuilder(String name) {
    super();
    if (name == null) {
      throw new IllegalArgumentException("name must not be null");
    }
    this.object().setName(name);
  }

  /**
   * Static utility method that creates a default {@code AttributeBuilder}.
   * 
   * @param name
   *          the attribute name
   * @return an {@code AttributeBuilder} instance.
   * @see #AttributeBuilder(String)
   */
  public static AttributeBuilder builder(String name) {
    return new AttributeBuilder(name);
  }

  /**
   * Constructor setting up the builder given an attribute template.
   * 
   * @param template
   *          the attribute template
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws MarshallingException
   *           for marshalling errors
   */
  public AttributeBuilder(Attribute template) throws MarshallingException, UnmarshallingException {
    super(template);
  }

  /**
   * Static utility method that creates a {@code AttributeBuilder} given a template attribute.
   * 
   * @param attribute
   *          the attribute template
   * @return an attribute builder
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws MarshallingException
   *           for marshalling errors
   */
  public static AttributeBuilder builder(Attribute attribute) throws MarshallingException, UnmarshallingException {
    return new AttributeBuilder(attribute);
  }

  /**
   * Constructor setting up the builder with a template attribute that is read from an input stream.
   * 
   * @param resource
   *          the attribute template
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws XMLParserException
   *           for XML parsing errors
   * @see AbstractSAMLObjectBuilder#AbstractSAMLObjectBuilder(InputStream)
   */
  public AttributeBuilder(InputStream resource) throws XMLParserException, UnmarshallingException {
    super(resource);
  }

  /**
   * Static utility method that creates a {@code AttributeBuilder} given a template attribute read from an input stream.
   * 
   * @param resource
   *          the attribute template
   * @return an attribute builder
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws XMLParserException
   *           for XML parsing errors
   */
  public static AttributeBuilder builder(InputStream resource) throws XMLParserException, UnmarshallingException {
    return new AttributeBuilder(resource);
  }

  /**
   * Returns the {@link Attribute} object that has been built. If the {@code NameFormat} attribute has not been
   * assigned, the method will inject the default value ({@value #DEFAULT_NAME_FORMAT}).
   */
  @Override
  public Attribute build() {
    if (this.object().getName() == null) {
      throw new RuntimeException("Attribute can not be built without a name");
    }
    if (this.object().getNameFormat() == null) {
      this.object().setNameFormat(DEFAULT_NAME_FORMAT);
    }
    return super.build();
  }

  /**
   * Assigns the attribute name.
   * 
   * @param name
   *          the attribute name
   * @return the builder
   */
  public AttributeBuilder name(String name) {
    this.object().setName(name);
    return this;
  }

  /**
   * Assigns the attribute friendly name.
   * 
   * @param friendlyName
   *          the friendly name
   * @return the builder
   */
  public AttributeBuilder friendlyName(String friendlyName) {
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
  public AttributeBuilder nameFormat(String nameFormat) {
    this.object().setNameFormat(nameFormat);
    return this;
  }

  /**
   * Assigns one (or more) attribute string values.
   * 
   * <p>
   * Note: if {@code null} is passed as a parameter, any previous attribute values are cleared.
   * </p>
   * 
   * @param values
   *          the string value(s) to add
   * @return the builder
   */
  public AttributeBuilder value(String... values) {
    return this.value(values != null ? Arrays.asList(values) : null);
  }

  /**
   * @see #value(String...)
   * 
   * @param values
   *          the string value(s) to add
   * @return the builder
   */
  public AttributeBuilder value(List<String> values) {
    if (values == null) {
      this.object().getAttributeValues().clear();
      return this;
    }
    for (String s : values) {
      XSString sv = createValueObject(XSString.TYPE_NAME, XSString.class);
      sv.setValue(s);
      this.object().getAttributeValues().add(sv);
    }
    return this;
  }

  /**
   * Assigns an attribute value.
   * 
   * @param value
   *          the value to add
   * @param <T>
   *          the value type
   * @return the builder
   */
  public <T extends XMLObject> AttributeBuilder value(T value) {
    this.object().getAttributeValues().add(value);
    return this;
  }

  /**
   * Creates an {@code AttributeValue} object of the given class. The type of the attribute value will be the field that
   * is declared as {@code TYPE_NAME} of the given class.
   * <p>
   * After the object has been constructed, its setter methods should be called to setup the value object before adding
   * it to the attribute itself.
   * </p>
   * <p>
   * Note: For attribute having string values, there is no need to explictly create an attribute value. Instead the
   * {@link #value(String...)} method may be used directly.
   * </p>
   *
   * @param <T>
   *          the type
   * @param clazz
   *          the type of attribute value
   * @return the attribute value
   * @see #createValueObject(QName, Class)
   */
  public static <T extends XMLObject> T createValueObject(Class<T> clazz) {
    try {
      QName schemaType = (QName) clazz.getDeclaredField("TYPE_NAME").get(null);
      return createValueObject(schemaType, clazz);
    }
    catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | SecurityException e) {
      throw new XMLRuntimeException(e);
    }
  }

  /**
   * Creates an {@code AttributeValue} object of the given class and schema type.
   * <p>
   * After the object has been constructed, its setter methods should be called to setup the value object before adding
   * it to the attribute itself.
   * </p>
   * <p>
   * Note: For attribute having string values, there is no need to explictly create an attribute value. Instead the
   * {@link #value(String...)} method may be used directly.
   * </p>
   * 
   * @param <T>
   *          the type
   * @param schemaType
   *          the schema type that should be assigned to the attribute value, i.e., {@code xsi:type="ns:ValueType"}
   * @param clazz
   *          the type of the attribute value
   * @return the attribute value
   * @see #createValueObject(Class)
   */
  public static <T extends XMLObject> T createValueObject(QName schemaType, Class<T> clazz) {
    XMLObjectBuilder<T> builder = ObjectUtils.getBuilder(schemaType);
    return builder.buildObject(AttributeValue.DEFAULT_ELEMENT_NAME, schemaType);
  }

  /** {@inheritDoc} */
  @Override
  protected Class<Attribute> getObjectType() {
    return Attribute.class;
  }

}
