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

import java.util.List;

import org.opensaml.saml.saml2.metadata.Organization;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.LocalizedString;
import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.OrganizationBuilder;

/**
 * A Spring factory bean for creating {@link Organization} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see OrganizationBuilder
 */
public class OrganizationFactoryBean extends AbstractSAMLObjectBuilderFactoryBean<Organization> {

  /** The builder. */
  private OrganizationBuilder builder;

  /**
   * Constructor.
   */
  public OrganizationFactoryBean() {
    this.builder = new OrganizationBuilder();
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return Organization.class;
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<Organization> builder() {
    return this.builder;
  }

  /**
   * Assigns the {@code OrganizationName} elements.
   * 
   * @param organizationNames
   *          the names
   * @see OrganizationBuilder#organizationNames(LocalizedString...)
   */
  public void setOrganizationNames(List<LocalizedString> organizationNames) {
    this.builder.organizationNames(localizedStringListToVarArgs(organizationNames));
  }

  /**
   * Assigns one {@code OrganizationName} element.
   * 
   * @param organizationName
   *          the name
   * @see OrganizationBuilder#organizationNames(LocalizedString...)
   */
  public void setOrganizationName(LocalizedString organizationName) {
    this.builder.organizationNames(organizationName);
  }

  /**
   * Assigns the {@code OrganizationDisplayName} elements.
   * 
   * @param organizationDisplayNames
   *          the names
   * @see OrganizationBuilder#organizationDisplayNames(LocalizedString...)
   */
  public void setOrganizationDisplayNames(List<LocalizedString> organizationDisplayNames) {
    this.builder.organizationDisplayNames(localizedStringListToVarArgs(organizationDisplayNames));
  }

  /**
   * Assigns one {@code OrganizationDisplayName} element.
   * 
   * @param organizationDisplayName
   *          the name
   * @see OrganizationBuilder#organizationDisplayNames(LocalizedString...)
   */
  public void setOrganizationDisplayName(LocalizedString organizationDisplayName) {
    this.builder.organizationDisplayNames(organizationDisplayName);
  }

  /**
   * Assigns the {@code OrganizationURL} elements.
   * 
   * @param organizationURLs
   *          the URLs
   * @see OrganizationBuilder#organizationURLs(LocalizedString...)
   */
  public void setOrganizationURLs(List<LocalizedString> organizationURLs) {
    this.builder.organizationURLs(localizedStringListToVarArgs(organizationURLs));
  }

  /**
   * Assigns one {@code OrganizationURL} element.
   * 
   * @param organizationURL
   *          the URL
   * @see OrganizationBuilder#organizationURLs(LocalizedString...)
   */
  public void setOrganizationURL(LocalizedString organizationURL) {
    this.builder.organizationURLs(organizationURL);
  }

}
