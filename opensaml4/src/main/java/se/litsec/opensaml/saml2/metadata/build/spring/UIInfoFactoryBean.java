/*
 * Copyright 2016-2021 Litsec AB
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

import java.util.List;
import java.util.Map;

import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.ext.saml2mdui.UIInfo;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.LocalizedString;
import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.UIInfoBuilder;

/**
 * A Spring factory bean for creating {@link UIInfo} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see UIInfoBuilder
 */
public class UIInfoFactoryBean extends AbstractSAMLObjectBuilderFactoryBean<UIInfo> {

  /** The builder. */
  private UIInfoBuilder builder;
  
  /**
   * Constructor.
   */
  public UIInfoFactoryBean() {
    this.builder = UIInfoBuilder.builder();
  }
  
  /**
   * Assigns the display names.
   * 
   * @param displayNames
   *          the names
   */
  public void setDisplayNames(final List<LocalizedString> displayNames) {
    this.builder.displayNames(displayNames);
  }
  
  /**
   * Assigns the keywords.
   * 
   * @param keywords
   *          the keywords where the map keys are language tags
   */
  public void setKeywords(final Map<String, List<String>> keywords) {
    this.builder.keywords(keywords);
  }
    
  /**
   * Assigns the descriptions.
   * 
   * @param descriptions
   *          the descriptions
   */
  public void setDescriptions(final List<LocalizedString> descriptions) {
    this.builder.descriptions(descriptions);
  }
  
  /**
   * Assigns the logotypes.
   * 
   * @param logos
   *          the logos (will be cloned before assignment)
   */
  public void setLogos(final List<Logo> logos) {
    this.builder.logos(logos);
  }
  
  /**
   * Assigns the information URL:s.
   * 
   * @param informationURLs
   *          the information URL:s
   */
  public void setInformationURLs(final List<LocalizedString> informationURLs) {
    this.builder.informationURLs(informationURLs);
  }
  
  /**
   * Assigns the privacy statement URL:s.
   * 
   * @param privacyStatementURLs
   *          the URL:s
   */
  public void setPrivacyStatementURLs(final List<LocalizedString> privacyStatementURLs) {
    this.builder.privacyStatementURLs(privacyStatementURLs);
  }
  
  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<UIInfo> builder() {
    return this.builder;
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return UIInfo.class;
  }

}
