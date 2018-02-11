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
package se.litsec.opensaml.saml2.metadata.build.spring;


import org.opensaml.saml.ext.saml2mdui.Logo;
import org.springframework.util.StringUtils;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.LogoBuilder;

/**
 * A Spring factory bean for creating {@link Logo} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see LogoBuilder
 */
public class LogoFactoryBean extends AbstractSAMLObjectBuilderFactoryBean<Logo> {

  /** The builder. */
  private LogoBuilder builder;

  /**
   * Constructor setting the the URL, height and width, but no language tag.
   * 
   * @param url
   *          the logo URL
   * @param height
   *          the height in pixels
   * @param width
   *          the width in pixels
   */
  public LogoFactoryBean(String url, Integer height, Integer width) {
    this(url, null, height, width);
  }

  /**
   * Constructor setting the the URL, its language tag and the height and width.
   * 
   * @param url
   *          the logo URL
   * @param language
   *          the language tag
   * @param height
   *          the height in pixels
   * @param width
   *          the width in pixels
   */
  public LogoFactoryBean(String url, String language, Integer height, Integer width) {
    this.builder = new LogoBuilder();
    this.builder
      .url(StringUtils.trimAllWhitespace(url))
      .language(StringUtils.trimAllWhitespace(language))
      .height(height)
      .width(width);
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return Logo.class;
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<Logo> builder() {
    return this.builder;
  }

}
