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

import org.opensaml.core.xml.schema.XSBooleanValue;
import org.springframework.core.convert.converter.Converter;

/**
 * A Spring converter bean that converts string values into OpenSAML {@code XSBooleanValue} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class StringToXSBooleanValueConverter implements Converter<String, XSBooleanValue> {

  /** {@inheritDoc} */
  @Override
  public XSBooleanValue convert(String source) {
    XSBooleanValue b = new XSBooleanValue();
    Boolean _b = Boolean.valueOf(source);
    b.setValue(_b);
    return b;
  }

}
