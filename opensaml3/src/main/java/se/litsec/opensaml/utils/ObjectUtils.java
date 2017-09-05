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

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.XMLObjectBuilder;
import org.opensaml.core.xml.XMLObjectBuilderFactory;
import org.opensaml.core.xml.XMLRuntimeException;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.Unmarshaller;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLObject;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.xml.XMLParserException;

/**
 * Utility methods for creating OpenSAML objects within directly having to make use of the builders for each object you
 * are creating and methods for marshalling and unmarshalling.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ObjectUtils {

  /**
   * Utility method for creating an OpenSAML {@code SAMLObject} using the default element name of the class.
   * <p>
   * Note: The field DEFAULT_ELEMENT_NAME of the given class will be used as the object's element name.
   * </p>
   * 
   * @param clazz
   *          the class to create
   * @param <T>
   *          the type of the class to create
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
   * @param <T>
   *          the type
   * @return the SAML object
   */
  public static <T extends SAMLObject> T createSamlObject(Class<T> clazz, QName elementName) {
    XMLObjectBuilderFactory builderFactory = XMLObjectProviderRegistrySupport.getBuilderFactory();
    XMLObjectBuilder<? extends XMLObject> builder = builderFactory.getBuilder(elementName);
    if (builder == null) {
      // No builder registered for the given element name. Try creating a builder for the default element name.
      QName defaultElementName = getDefaultElementName(clazz);
      if (!defaultElementName.equals(elementName)) {
        builder = builderFactory.getBuilder(defaultElementName);
      }
    }
    if (builder == null) {
      // Still no builder? Time to fail.
      throw new XMLRuntimeException("No builder registered for " + clazz.getName());
    }
    Object object = builder.buildObject(elementName);
    return clazz.cast(object);
  }

  /**
   * Utility method for creating an {@code XMLObject} given its element name.
   * 
   * @param clazz
   *          the class to create
   * @param elementName
   *          the element name for the XML object to create
   * @param <T>
   *          the type
   * @return the XML object
   */
  public static <T extends XMLObject> T createXMLObject(Class<T> clazz, QName elementName) {
    return XMLObjectProviderRegistrySupport.getBuilderFactory().<T> getBuilderOrThrow(elementName).buildObject(elementName);
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
   * @param <T>
   *          the type
   * @return the XML object
   */
  public static <T extends XMLObject> T createXMLObject(Class<T> clazz, QName registeredElementName, QName elementNameToAssign) {
    return XMLObjectProviderRegistrySupport.getBuilderFactory().<T> getBuilderOrThrow(registeredElementName).buildObject(
      elementNameToAssign);
  }

  /**
   * Returns the default element name for the supplied class
   * 
   * @param clazz
   *          class to check
   * @param <T>
   *          the type
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
   * @param <T>
   *          the type
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
   * @param <T>
   *          the type
   * @return a builder object
   */
  @SuppressWarnings("unchecked")
  public static <T extends XMLObject> XMLObjectBuilder<T> getBuilder(QName elementName) {
    return (XMLObjectBuilder<T>) XMLObjectSupport.getBuilder(elementName);
  }

  /**
   * Marshalls the supplied {@code XMLObject} into an {@code Element}.
   * 
   * @param object
   *          the object to marshall
   * @param <T>
   *          the type
   * @return an XML element
   * @throws MarshallingException
   *           for marshalling errors
   */
  public static <T extends XMLObject> Element marshall(T object) throws MarshallingException {
    return XMLObjectSupport.marshall(object);
  }

  /**
   * Unmarshalls the supplied element into the given type.
   * 
   * @param xml
   *          the DOM (XML) to unmarshall
   * @param targetClass
   *          the required class
   * @param <T>
   *          the type
   * @return an {@code XMLObject} of the given type
   * @throws UnmarshallingException
   *           for unmarshalling errors
   */
  public static <T extends XMLObject> T unmarshall(Element xml, Class<T> targetClass) throws UnmarshallingException {
    Unmarshaller unmarshaller = XMLObjectSupport.getUnmarshaller(xml);
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
   * @param <T>
   *          the type
   * @return an {@code XMLObject} of the given type
   * @throws XMLParserException
   *           for XML parsing errors
   * @throws UnmarshallingException
   *           for unmarshalling errors
   */
  public static <T extends XMLObject> T unmarshall(InputStream inputStream, Class<T> targetClass) throws XMLParserException,
      UnmarshallingException {
    return unmarshall(XMLObjectProviderRegistrySupport.getParserPool().parse(inputStream).getDocumentElement(), targetClass);
  }

  // Hidden constructor.
  private ObjectUtils() {
  }

}
