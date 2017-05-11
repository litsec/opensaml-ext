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

import java.util.Arrays;
import java.util.List;

import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.OrganizationDisplayName;
import org.opensaml.saml.saml2.metadata.OrganizationName;
import org.opensaml.saml.saml2.metadata.OrganizationURL;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.LocalizedString;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * A builder for {@code Organization} elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class OrganizationBuilder extends AbstractSAMLObjectBuilder<Organization> {

  /**
   * Utility method that creates a builder.
   * 
   * @return a builder
   */  
  public static OrganizationBuilder builder() {
    return new OrganizationBuilder();
  }
  
  /** {@inheritDoc} */
  @Override
  protected Class<Organization> getObjectType() {
    return Organization.class;
  }

  /**
   * Assigns the {@code OrganizationName} elements.
   * 
   * @param organizationNames
   *          the names
   * @return the builder
   */
  public OrganizationBuilder organizationNames(List<LocalizedString> organizationNames) {
    if (organizationNames != null) {
      for (LocalizedString s : organizationNames) {
        OrganizationName on = ObjectUtils.createSamlObject(OrganizationName.class);
        on.setValue(s.getLocalString());
        on.setXMLLang(s.getLanguage());
        this.object().getOrganizationNames().add(on);
      }
    }
    return this;
  }

  /**
   * @see #organizationNames(List)
   * 
   * @param organizationNames
   *          the names
   * @return the builder
   */
  public OrganizationBuilder organizationNames(LocalizedString... organizationNames) {
    return this.organizationNames(organizationNames != null ? Arrays.asList(organizationNames) : null);
  }

  /**
   * Assigns the {@code OrganizationDisplayName} elements
   * 
   * @param organizationDisplayNames
   *          the names
   * @return the builder
   */
  public OrganizationBuilder organizationDisplayNames(List<LocalizedString> organizationDisplayNames) {
    if (organizationDisplayNames != null) {
      for (LocalizedString s : organizationDisplayNames) {
        OrganizationDisplayName on = ObjectUtils.createSamlObject(OrganizationDisplayName.class);
        on.setValue(s.getLocalString());
        on.setXMLLang(s.getLanguage());
        this.object().getDisplayNames().add(on);
      }
    }
    return this;
  }

  /**
   * @see #organizationDisplayNames(List)
   * 
   * @param organizationDisplayNames
   *          the names
   * @return the builder
   */
  public OrganizationBuilder organizationDisplayNames(LocalizedString... organizationDisplayNames) {
    return this.organizationDisplayNames(organizationDisplayNames != null ? Arrays.asList(organizationDisplayNames) : null);
  }

  /**
   * Assigns the {@code OrganizationURL} elements
   * 
   * @param organizationURLs
   *          the URLs
   * @return the builder
   */
  public OrganizationBuilder organizationURLs(List<LocalizedString> organizationURLs) {
    if (organizationURLs != null) {
      for (LocalizedString s : organizationURLs) {
        OrganizationURL on = ObjectUtils.createSamlObject(OrganizationURL.class);
        on.setValue(s.getLocalString());
        on.setXMLLang(s.getLanguage());
        this.object().getURLs().add(on);
      }
    }
    return this;
  }

  /**
   * @see #organizationURLs(List)
   * 
   * @param organizationURLs
   *          the URLs
   * @return the builder
   */
  public OrganizationBuilder organizationURLs(LocalizedString... organizationURLs) {
    return this.organizationURLs(organizationURLs != null ? Arrays.asList(organizationURLs) : null);
  }

}
