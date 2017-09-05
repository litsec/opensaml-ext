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
  public void setDisplayNames(List<LocalizedString> displayNames) {
    this.builder.displayNames(displayNames);
  }
  
  /**
   * Assigns the keywords.
   * 
   * @param keywords
   *          the keywords where the map keys are language tags
   */
  public void setKeywords(Map<String, List<String>> keywords) {
    this.builder.keywords(keywords);
  }
  
  /**
   * Assigns a set of keywords that do not have the language tag.
   * 
   * @param keywords
   *          the keywords
   */
  public void setKeywords(List<String> keywords) {
    this.builder.keywords(keywords);
  }
  
  /**
   * Assigns the descriptions.
   * 
   * @param descriptions
   *          the descriptions
   */
  public void setDescriptions(List<LocalizedString> descriptions) {
    this.builder.descriptions(descriptions);
  }
  
  /**
   * Assigns the logotypes.
   * 
   * @param logos
   *          the logos (will be cloned before assignment)
   */
  public void setLogos(List<Logo> logos) {
    this.builder.logos(logos);
  }
  
  /**
   * Assigns the information URL:s.
   * 
   * @param informationURLs
   *          the information URL:s
   */
  public void setInformationURLs(List<LocalizedString> informationURLs) {
    this.builder.informationURLs(informationURLs);
  }
  
  /**
   * Assigns the privacy statement URL:s.
   * 
   * @param privacyStatementURLs
   *          the URL:s
   */
  public void setPrivacyStatementURLs(List<LocalizedString> privacyStatementURLs) {
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
