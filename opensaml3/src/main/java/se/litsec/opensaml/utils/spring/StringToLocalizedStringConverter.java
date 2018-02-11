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
   * Converts strings on the format {@code <lang-tag>-<string according to language>}. The string "en-Hello" will give a
   * LocalizedString where:
   * 
   * <pre>{@code
   * ls.getLanguage() => "en"
   * ls.getLocalString() => "Hello"}
   * </pre>
   */
  @Override
  public LocalizedString convert(String source) {    
    return new LocalizedString(source);
  }

}
