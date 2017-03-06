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
package se.litsec.opensaml.utils.spring;

import org.opensaml.saml.common.SAMLObject;
import org.springframework.core.convert.converter.Converter;

import se.litsec.opensaml.utils.ObjectUtils;


/**
 * An abstract Spring converter class for transforming string values into OpenSAML objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * 
 * @param <T>
 *          the XML type
 */
public abstract class StringToXMLObjectConverter<T extends SAMLObject> implements Converter<String, T> {

  /** The class. */
  protected Class<T> clazz;

  /**
   * Constructor.
   * 
   * @param clazz
   *          the class name of the OpenSAML object to convert to
   */
  public StringToXMLObjectConverter(Class<T> clazz) {
    this.clazz = clazz;
  }

  /** {@inheritDoc} */
  @Override
  public T convert(String source) {
    T obj = ObjectUtils.createSamlObject(this.clazz);
    this.assign(obj, source);
    return obj;
  }

  /**
   * Assigns the given value to the OpenSAML object (after conversion).
   * 
   * @param obj
   *          the OpenSAML object that we should assign the value to
   * @param value
   *          the value to assign
   */
  protected abstract void assign(T obj, String value);

}
