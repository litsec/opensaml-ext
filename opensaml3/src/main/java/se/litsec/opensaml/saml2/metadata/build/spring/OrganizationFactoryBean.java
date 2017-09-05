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
