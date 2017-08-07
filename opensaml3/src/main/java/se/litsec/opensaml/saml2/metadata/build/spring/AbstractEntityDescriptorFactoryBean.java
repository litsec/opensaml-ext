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

import org.opensaml.saml.ext.saml2mdui.UIInfo;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.SingleLogoutService;

import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.AbstractEntityDescriptorBuilder;

/**
 * Abstract base factory bean for building {@link EntityDescriptor} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 *
 * @param <T>
 *          the type of builder being used
 */
public abstract class AbstractEntityDescriptorFactoryBean<T extends AbstractEntityDescriptorBuilder<?>> extends
    AbstractSAMLObjectBuilderFactoryBean<EntityDescriptor> {

  /**
   * Returns the internal builder of the correct type.
   * 
   * @return the builder to use
   */
  protected abstract T _builder();

  /**
   * Assigns the entityID for the {@code EntityDescriptor}.
   * 
   * @param entityID
   *          the entityID
   */
  public void setEntityID(String entityID) {
    this._builder().entityID(entityID);
  }

  /**
   * Assigns the ID attribute for the {@code EntityDescriptor}.
   * 
   * @param id
   *          the ID
   */
  public void setID(String id) {
    this._builder().id(id);
  }

  /**
   * Assigns the cacheDuration attribute for the {@code EntityDescriptor}.
   * 
   * @param cacheDuration
   *          the cache duration (in milliseconds)
   */
  public void setCacheDuration(Long cacheDuration) {
    this._builder().cacheDuration(cacheDuration);
  }

  /**
   * Assigns the valid until time.
   * 
   * @param time
   *          valid until
   */
  public void setValidUntil(org.joda.time.DateTime time) {
    this._builder().validUntil(time);
  }

  /**
   * Assigns the valid until time (using the local time zone).
   * 
   * @param time
   *          valid until
   */
  public void setValidUntil(java.time.LocalDateTime time) {
    this._builder().validUntil(time);
  }

  /**
   * Adds attributes to the {@code mdattr:EntityAttributes} element that is part of the metadata extension element.
   * 
   * @param attributes
   *          a list of attributes
   * @see #setEntityCategories(List)
   */
  public void setEntityAttributesExtension(List<Attribute> attributes) {
    this._builder().entityAttributesExtension(attributes);
  }

  /**
   * Adds a set of entity categories to the entity category attribute ({@code http://macedir.org/entity-category}) that
   * is part of the {@code mdattr:EntityAttributes} element that is part of the metadata extension element.
   * 
   * @param entityCategories
   *          the entity category values that should be added
   * @see #setEntityAttributesExtension(List)
   */
  public void setEntityCategories(List<String> entityCategories) {
    this._builder().entityCategories(entityCategories);
  }
  
  /**
   * Assigns the {@code mdui:UIInfo} element as an extension to the role descriptor.
   * 
   * @param uiInfo
   *          the {@code UIInfo} element (will be cloned before assignment)
   */
  public void setUiInfoExtension(UIInfo uiInfo) {
    this._builder().uiInfoExtension(uiInfo);
  }
  
  /**
   * Adds the key descriptor elements.
   * 
   * @param keyDescriptors
   *          the key descriptors
   */
  public void setKeyDescriptors(List<KeyDescriptor> keyDescriptors) {
    this._builder().keyDescriptors(keyDescriptors);
  }

  /**
   * Assigns the {@code md:NameIDFormat} elements.
   * 
   * @param nameIDFormats
   *          the nameID format strings
   */
  public void setNameIDFormats(List<String> nameIDFormats) {
    this._builder().nameIDFormats(nameIDFormats);
  }
  
  /**
   * Adds {@code md:SingleLogoutService} elements to the {@code SSODescriptor}.
   * 
   * @param singleLogoutServices
   *          single logout service objects (cloned before assignment)
   */
  public void setSingleLogoutServices(List<SingleLogoutService> singleLogoutServices) {
    this._builder().singleLogoutServices(singleLogoutServices);
  }
  
  /**
   * Assigns the {@code Organization} element to the entity descriptor.
   * 
   * @param organization
   *          the organization (will be cloned before assignment)
   */
  public void setOrganization(Organization organization) {
    this._builder().organization(organization);
  }
  
  /**
   * Assigns the {@code ContactPerson} elements to the entity descriptor.
   * 
   * @param contactPersons
   *          the contact person elements (will be cloned before assignment)
   */
  public void setContactPersons(List<ContactPerson> contactPersons) {
    this._builder().contactPersons(contactPersons);
  }
  
  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return EntityDescriptor.class;
  }

}
