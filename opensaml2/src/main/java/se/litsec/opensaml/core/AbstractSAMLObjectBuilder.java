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
package se.litsec.opensaml.core;

import java.io.InputStream;

import org.opensaml.common.SAMLObject;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.XMLObjectHelper;

import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Abstract base class for the builder pattern.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 *
 * @param <T>
 *          the type
 */
public abstract class AbstractSAMLObjectBuilder<T extends SAMLObject> implements SAMLObjectBuilder<T> {

  /** The object that is being built. */
  private T object;

  /**
   * Constructor setting up the object to build.
   */
  public AbstractSAMLObjectBuilder() {
    this.object = ObjectUtils.createSamlObject(this.getObjectType());
  }

  /**
   * Constructor setting up the builder with a template object. Users of the instance may now change, add or delete, the
   * elements and attributes of the template object using the assignment methods of the builder.
   * <p>
   * Note that the supplied object is cloned, so any modifications have no effect on the passed object.
   * </p>
   * 
   * @param template
   *          the template object
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws MarshallingException
   *           for marshalling errors
   */
  public AbstractSAMLObjectBuilder(T template) throws MarshallingException, UnmarshallingException {
    this.object = XMLObjectHelper.cloneXMLObject(template, true); 
  }

  /**
   * Constructor setting up the builder with a template object that is read from an input stream. Users of the instance
   * may now change, add or delete, the elements and attributes of the template object using the assignment methods of
   * the builder.
   * 
   * @param resource
   *          the template resource
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws XMLParserException
   *           for XML parsing errors
   */
  public AbstractSAMLObjectBuilder(InputStream resource) throws XMLParserException, UnmarshallingException {
    this.object = ObjectUtils.unmarshall(resource, this.getObjectType());
  }

  /**
   * The default implementation of this method assumes that the object has been built during assignment of its
   * attributes and elements so it simply returns the object.
   * <p>
   * Implementations that need to perform additional processing during the build step should override this method.
   * </p>
   */
  @Override
  public T build() {
    return this.object();
  }

  /**
   * Returns the object type.
   * 
   * @return the object type
   */
  protected abstract Class<T> getObjectType();

  /**
   * Returns the object being built.
   * 
   * @return the object
   */
  protected final T object() {
    return this.object;
  }

}
