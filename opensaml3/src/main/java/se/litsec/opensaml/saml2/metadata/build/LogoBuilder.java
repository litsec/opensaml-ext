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
package se.litsec.opensaml.saml2.metadata.build;

import org.opensaml.saml.ext.saml2mdui.Logo;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;

/**
 * A builder for {@code mdui:Logo} elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class LogoBuilder extends AbstractSAMLObjectBuilder<Logo> {

  /**
   * Creates a new {@code LogoBuilder} instance.
   * 
   * @return a {@code LogoBuilder} instance
   */
  public static LogoBuilder builder() {
    return new LogoBuilder();
  }

  /**
   * Utility method that builds a {@code mdui:Logo} object.
   * 
   * @param url
   *          the Logo URL
   * @param height
   *          the height
   * @param width
   *          the width
   * @return a {@code Logo} instance
   */
  public static Logo logo(String url, Integer height, Integer width) {
    return builder()
      .url(url)
      .height(height)
      .width(width)
      .build();
  }
  
  /**
   * Utility method that builds a {@code mdui:Logo} object.
   * 
   * @param url
   *          the Logo URL
   * @param language
   *          the language
   * @param height
   *          the height
   * @param width
   *          the width
   * @return a {@code Logo} instance
   */
  public static Logo logo(String url, String language, Integer height, Integer width) {
    return builder()
      .url(url)
      .language(language)
      .height(height)
      .width(width)
      .build();
  }

  /** {@inheritDoc} */
  @Override
  protected Class<Logo> getObjectType() {
    return Logo.class;
  }

  /**
   * Assigns the URL of the {@code Logo}.
   * 
   * @param url
   *          the URL
   * @return the builder
   */
  public LogoBuilder url(String url) {
    this.object().setURL(url);
    return this;
  }

  /**
   * Assigns the language tag of the {@code Logo}.
   * 
   * @param language
   *          the language tag
   * @return the builder
   */
  public LogoBuilder language(String language) {
    this.object().setXMLLang(language);
    return this;
  }

  /**
   * Assigns the height of the {@code Logo}.
   * 
   * @param height
   *          the height (in pixels)
   * @return the builder
   */
  public LogoBuilder height(Integer height) {
    this.object().setHeight(height);
    return this;
  }

  /**
   * Assigns the width of the {@code Logo}.
   * 
   * @param width
   *          the width (in pixels)
   * @return the builder
   */
  public LogoBuilder width(Integer width) {
    this.object().setWidth(width);
    return this;
  }

}
