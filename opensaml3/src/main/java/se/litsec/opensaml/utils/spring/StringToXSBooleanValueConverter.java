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
    Boolean _b = new Boolean(source);
    b.setValue(_b);
    return b;
  }

}
