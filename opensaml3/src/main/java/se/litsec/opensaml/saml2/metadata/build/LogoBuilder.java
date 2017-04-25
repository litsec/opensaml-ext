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
