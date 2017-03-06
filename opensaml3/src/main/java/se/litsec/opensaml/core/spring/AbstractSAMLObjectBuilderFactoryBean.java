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
