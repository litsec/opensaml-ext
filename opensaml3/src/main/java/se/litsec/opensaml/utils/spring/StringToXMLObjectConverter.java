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
