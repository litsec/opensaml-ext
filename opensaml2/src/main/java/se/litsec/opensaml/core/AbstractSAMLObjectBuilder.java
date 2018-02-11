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
