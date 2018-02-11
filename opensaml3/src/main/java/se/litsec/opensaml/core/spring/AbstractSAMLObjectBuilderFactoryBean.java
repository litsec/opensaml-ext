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
package se.litsec.opensaml.core.spring;

import java.lang.reflect.Array;
import java.util.List;

import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLObject;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.LocalizedString;
import se.litsec.opensaml.core.SAMLObjectBuilder;

/**
 * Abstract base class for factory beans that are implemented using the builder pattern defined in
 * {@link SAMLObjectBuilder} interface.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 *
 * @param <T>
 *          the type
 */
public abstract class AbstractSAMLObjectBuilderFactoryBean<T extends SAMLObject> extends AbstractFactoryBean<T> {

  /**
   * The default implementation assumes that the object has been set up when elements and attributes were assigned, and
   * simply returns the build object (if this is not a singleton bean, the object is cloned).
   */
  @Override
  protected T createInstance() throws Exception {
    return this.isSingleton() ? this.builder().build() : XMLObjectSupport.cloneXMLObject(this.builder().build());
  }

  /**
   * Returns the builder.
   * 
   * @return the builder
   */
  protected abstract AbstractSAMLObjectBuilder<T> builder();

  /**
   * Utility method that transforms a list into a varargs array (for usage in calls to builder instances).
   * 
   * @param list
   *          the list to transform
   * @param cls
   *          the type of elements in the list
   * @param <V>
   *          the type of objects stored in the array
   * @return an array
   */
  @SuppressWarnings("unchecked")
  protected static <V> V[] toVarArgs(List<V> list, Class<V> cls) {
    return list != null ? list.toArray((V[]) Array.newInstance(cls, list.size())) : (V[]) null;
  }

  /**
   * Utility method that transforms a list of {@code LocalizedString} objects into a varargs array (for usage in calls
   * to builder instances).
   * 
   * @param list
   *          the list to transform
   * @return an array
   */
  protected static LocalizedString[] localizedStringListToVarArgs(List<LocalizedString> list) {
    if (list == null) {
      return null;
    }
    return list.isEmpty() ? null : list.toArray(new LocalizedString[list.size()]);
  }

  /**
   * Utility method that transforms a list of {@code String} objects into a varargs array (for usage in calls to builder
   * instances).
   * 
   * @param list
   *          the list to transform
   * @return an array
   */
  protected static String[] stringListToVarArgs(List<String> list) {
    if (list == null) {
      return null;
    }
    return list.isEmpty() ? null : list.toArray(new String[list.size()]);
  }

}
