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
package se.litsec.opensaml.utils;

import java.io.InputStream;

import javax.xml.namespace.QName;

import org.opensaml.common.SAMLObject;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.XMLObjectBuilder;
import org.opensaml.xml.XMLObjectBuilderFactory;
import org.opensaml.xml.XMLRuntimeException;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.XMLObjectHelper;
import org.w3c.dom.Element;

/**
 * Utility methods for creating OpenSAML objects within directly having to make use of the builders for each object you
 * are creating and methods for marshalling and unmarshalling.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ObjectUtils {

  /** The builder factory for XML objects. */
  private static XMLObjectBuilderFactory builderFactory = Configuration.getBuilderFactory();
  
  /** Parser pool to use when unmarshalling. */
  private static BasicParserPool parserPool = new BasicParserPool();
  
  static {
    parserPool.setNamespaceAware(true);
  }

  /**
   * Utility method for creating an OpenSAML {@code SAMLObject} using the default element name of the class.
   * <p>
   * Note: The field DEFAULT_ELEMENT_NAME of the given class will be used as the object's element name.
   * </p>
   * 
   * @param clazz
   *          the class to create
   * @return the SAML object
   * @see #createSamlObject(Class, QName)
   */
  public static <T extends SAMLObject> T createSamlObject(Class<T> clazz) {
    return createSamlObject(clazz, getDefaultElementName(clazz));
  }

  /**
   * Utility method for creating an OpenSAML {@code SAMLObject} given its element name.
   * 
   * @param clazz
   *          the class to create
   * @param elementName
   *          the element name to assign the object that is created.
   * @return the SAML object
   */
  public static <T extends SAMLObject> T createSamlObject(Class<T> clazz, QName elementName) {

    XMLObjectBuilder<?> builder = builderFactory.getBuilder(elementName);
    if (builder == null) {
      // No builder registered for the given element name. Try creating a builder for the default element name.
      builder = builderFactory.getBuilder(getDefaultElementName(clazz));
    }
    if (builder == null) {
      // Still no builder? Time to fail.
      throw new XMLRuntimeException("No builder registered for " + clazz.getName());
    }
    XMLObject object = builder.buildObject(elementName);
    return clazz.cast(object);
  }

  /**
   * Utility method for creating an {@code XMLObject} given its element name.
   * 
   * @param clazz
   *          the class to create
   * @param elementName
   *          the element name for the XML object to create
   * @return the XML object
   */
  public static <T extends XMLObject> T createXMLObject(Class<T> clazz, QName elementName) {

    XMLObjectBuilder<?> builder = builderFactory.getBuilder(elementName);
    if (builder == null) {
      // No builder registered for the given element name.
      throw new XMLRuntimeException("No builder registered for " + clazz.getName());
    }
    Object object = builder.buildObject(elementName);
    return clazz.cast(object);
  }

  /**
   * Utility method for creating an {@code XMLObject} given its registered element name but where the
   * {@code elementNameToAssign} is assigned to the object created.
   * 
   * @param clazz
   *          the class to create
   * @param registeredElementName
   *          the element name that the object is registered under
   * @param elementNameToAssign
   *          the element to assign to the object that is created
   * @return the XML object
   */
  public static <T extends XMLObject> T createXMLObject(Class<T> clazz, QName registeredElementName, QName elementNameToAssign) {
    XMLObjectBuilder<?> builder = builderFactory.getBuilder(registeredElementName);
    if (builder == null) {
      // No builder registered for the given element name.
      throw new XMLRuntimeException("No builder registered for " + clazz.getName() + " using elementName " + registeredElementName.toString());
    }
    Object object = builder.buildObject(elementNameToAssign);
    return clazz.cast(object);
  }

  /**
   * Returns the default element name for the supplied class
   * 
   * @param clazz
   *          class to check
   * @return the default QName
   */
  public static <T extends SAMLObject> QName getDefaultElementName(Class<T> clazz) {
    try {
      return (QName) clazz.getDeclaredField("DEFAULT_ELEMENT_NAME").get(null);
    }
    catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException | SecurityException e) {
      throw new XMLRuntimeException(e);
    }
  }

  /**
   * Returns the builder object that can be used to create objects of the supplied class type.
   * <p>
   * Note: The field DEFAULT_ELEMENT_NAME of the given class will be used as the object's element name.
   * </p>
   * 
   * @param clazz
   *          the class which we want a builder for
   * @return a builder object
   * @see #getBuilder(QName)
   */
  public static <T extends SAMLObject> XMLObjectBuilder<T> getBuilder(Class<T> clazz) {
    return getBuilder(getDefaultElementName(clazz));
  }

  /**
   * Returns the builder object that can be used to build object for the given element name.
   * 
   * @param elementName
   *          the element name for the XML object that the builder should return
   * @return a builder object
   */
  @SuppressWarnings("unchecked")
  public static <T extends XMLObject> XMLObjectBuilder<T> getBuilder(QName elementName) {
    return builderFactory.getBuilder(elementName);
  }

  /**
   * Marshalls the supplied {@code XMLObject} into an {@code Element}.
   * 
   * @param object
   *          the object to marshall
   * @return an XML element
   * @throws MarshallingException
   *           for marshalling errors
   */
  public static <T extends XMLObject> Element marshall(T object) throws MarshallingException {
    Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(object);
    if (marshaller == null) {
      throw new MarshallingException("No marshaller found for " + object.getClass().getSimpleName());
    }
    return marshaller.marshall(object);
  }

  /**
   * Unmarshalls the supplied element into the given type.
   * 
   * @param xml
   *          the DOM (XML) to unmarshall
   * @param targetClass
   *          the required class
   * @return an {@code XMLObject} of the given type
   * @throws UnmarshallingException
   *           for unmarshalling errors
   */
  public static <T extends XMLObject> T unmarshall(Element xml, Class<T> targetClass) throws UnmarshallingException {
    Unmarshaller unmarshaller = Configuration.getUnmarshallerFactory().getUnmarshaller(xml);
    if (unmarshaller == null) {
      throw new UnmarshallingException("No unmarshaller found for " + xml.getNodeName());
    }
    XMLObject xmlObject = unmarshaller.unmarshall(xml);
    return targetClass.cast(xmlObject);
  }

  /**
   * Unmarshalls the supplied input stream into the given type.
   * 
   * @param inputStream
   *          the input stream of the XML resource
   * @param targetClass
   *          the required class
   * @return an {@code XMLObject} of the given type
   * @throws XMLParserException
   *           for XML parsing errors
   * @throws UnmarshallingException
   *           for unmarshalling errors
   */
  public static <T extends XMLObject> T unmarshall(InputStream inputStream, Class<T> targetClass) throws XMLParserException,
      UnmarshallingException {
    
    XMLObject object = XMLObjectHelper.unmarshallFromInputStream(parserPool, inputStream);
    return targetClass.cast(object);
  }

  // Hidden constructor.
  private ObjectUtils() {
  }

}
