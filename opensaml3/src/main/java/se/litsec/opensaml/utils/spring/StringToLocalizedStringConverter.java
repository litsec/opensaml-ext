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

import org.springframework.core.convert.converter.Converter;

import se.litsec.opensaml.core.LocalizedString;

/**
 * Utility class for transforming strings into OpenSAML LocalizedStrings.
 * <p>
 * The strings being converted MUST start with the language identifier followed by a hyphen. For example, the string
 * "en-Hello" will be translated to the localized string "Hello" in English. If no language indicator is given, a
 * {@code LocalizedString} with no language tag will be created.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class StringToLocalizedStringConverter implements Converter<String, LocalizedString> {

  /**
   * Converts strings on the format <lang-tag>-<string according to language>. The string "en-Hello" will give a
   * LocalizedString where:
   * 
   * <pre>
   * ls.getLanguage() => "en"
   * ls.getLocalString() => "Hello"
   * </pre>
   */
  @Override
  public LocalizedString convert(String source) {
    return LocalizedString.parse(source);
  }

}
