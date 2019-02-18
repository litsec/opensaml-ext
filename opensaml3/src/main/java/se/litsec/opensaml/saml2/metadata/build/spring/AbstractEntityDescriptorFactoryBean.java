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

import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
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
   * Adds a ordered list of {@code alg:SigningMethod} elements according to "SAML v2.0 Metadata Profile for Algorithm
   * Support Version 1.0" to the entity descriptor extensions element.
   * 
   * @param signingMethods
   *          the methods to add
   * @see #setSigningMethodsRole(List)
   */
  public void setSigningMethodsGlobal(List<SigningMethod> signingMethods) {
    this._builder().signingMethods(false, signingMethods);
  }

  /**
   * Adds a ordered list of {@code alg:SigningMethod} elements according to "SAML v2.0 Metadata Profile for Algorithm
   * Support Version 1.0" to the extensions element of the role descriptor. These extensions have precedence over those
   * given as extensions to the entity descriptor (see {@link #setSigningMethodsGlobal(List)}).
   * 
   * @param signingMethods
   *          the methods to add
   */
  public void setSigningMethodsRole(List<SigningMethod> signingMethods) {
    this._builder().signingMethods(true, signingMethods);
  }

  /**
   * Adds a ordered list of {@code alg:DigestMethod} elements according to "SAML v2.0 Metadata Profile for Algorithm
   * Support Version 1.0" to the entity descriptor extensions element.
   * 
   * @param digestMethods
   *          the methods to add
   * @see #setDigestMethodsRole(List)
   */  
  public void setDigestMethodsGlobal(List<DigestMethod> digestMethods) {
    this._builder().digestMethods(false, digestMethods);
  }

  /**
   * Adds a ordered list of {@code alg:DigestMethod} elements according to "SAML v2.0 Metadata Profile for Algorithm
   * Support Version 1.0" to the extensions element of the role descriptor. These extensions have precedence over those
   * given as extensions to the entity descriptor (see {@link #setDigestMethodsGlobal(List)}).
   * 
   * @param digestMethods
   *          the methods to add
   */  
  public void setDigestMethodsRole(List<DigestMethod> digestMethods) {
    this._builder().digestMethods(true, digestMethods);
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
