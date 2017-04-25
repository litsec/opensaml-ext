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

import java.io.IOException;
import java.io.InputStream;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.ext.saml2mdui.UIInfo;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.saml.saml2.metadata.Organization;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.springframework.core.io.Resource;

import net.shibboleth.utilities.java.support.xml.XMLParserException;
import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.saml2.attribute.AttributeTemplate;
import se.litsec.opensaml.saml2.metadata.MetadataUtils;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Abstract base builder for creating {@code EntityDescriptor} objects using the builder pattern, and optionally a
 * template object.
 * <p>
 * When a template object is used, the builder is created using the {@link #AbstractEntityDescriptorBuilder(Resource)}
 * or {@link #AbstractEntityDescriptorBuilder(EntityDescriptor)} constructors. The user may later change, or add, any of
 * the elements and attributes of the template object using the assignment methods.
 * </p>
 * <p>
 * Note that no Signature will be included.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * 
 * @param <T>
 *          the concrete builder type
 */
public abstract class AbstractEntityDescriptorBuilder<T extends AbstractSAMLObjectBuilder<EntityDescriptor>> extends
    AbstractSAMLObjectBuilder<EntityDescriptor> {

  /** The attribute name for the entity category attribute stored as an attribute in the entity attributes extension. */
  public static final String ENTITY_CATEGORY_ATTRIBUTE_NAME = "http://macedir.org/entity-category";

  /**
   * The attribute template for the entity category attribute stored as an attribute in the entity attributes extension.
   */
  public static final AttributeTemplate ENTITY_CATEGORY_TEMPLATE = new AttributeTemplate(ENTITY_CATEGORY_ATTRIBUTE_NAME, null);

  /**
   * Constructor setting up the builder with no template. This means that the entire {@code EntityDescriptor} object is
   * created from data assigned using the builder.
   */
  public AbstractEntityDescriptorBuilder() {
    super();
  }

  /**
   * Constructor setting up the builder with a template {@code EntityDescriptor} that is read from a resource. Users of
   * the bean may now change, add or delete, the elements and attributes of the template object using the assignment
   * methods of the builder.
   * 
   * @param resource
   *          the template resource
   * @throws IOException
   *           if the resource can not be read
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws XMLParserException
   *           for XML parsing errors
   */
  public AbstractEntityDescriptorBuilder(InputStream resource) throws XMLParserException, UnmarshallingException, IOException {
    super(resource);
    if (!this.matchingSSODescriptorType(this.object())) {
      throw new IllegalArgumentException("The SSO descriptor of the template does not match the builder type");
    }

    // Remove signature
    this.object().setSignature(null);
  }

  /**
   * Constructor setting up the builder with a template {@code EntityDescriptor}. Users of the bean may now change, add
   * or delete, the elements and attributes of the template object using the assignment methods of the builder.
   * 
   * @param template
   *          the template
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws MarshallingException
   *           for marshalling errors
   */
  public AbstractEntityDescriptorBuilder(EntityDescriptor template) throws UnmarshallingException, MarshallingException {
    super(template);
    if (!this.matchingSSODescriptorType(this.object())) {
      throw new IllegalArgumentException("The SSO descriptor of the template does not match the builder type");
    }

    // Remove signature
    this.object().setSignature(null);
  }

  /** {@inheritDoc} */
  @Override
  protected Class<EntityDescriptor> getObjectType() {
    return EntityDescriptor.class;
  }

  /**
   * In order for us to be able to make chaining calls we need to return the concrete type of the bulder.
   * 
   * @return the concrete type of the builder
   */
  protected abstract T getThis();

  /**
   * Returns the SSO role descriptor of the template entity descriptor. If no role descriptor is present, the method
   * creates such an object.
   * 
   * @return the role descriptor
   */
  protected abstract SSODescriptor ssoDescriptor();

  /**
   * Checks that the SSO descriptor of the supplied {@code EntityDescriptor} is of the correct type given the factory
   * bean type.
   * <p>
   * An entity descriptor that does not yet have an SSODescriptor element will also be regarded as a matching type.
   * </p>
   * 
   * @param descriptor
   *          the descriptor to check
   * @return {@code true} if the type is OK, and {@code false} otherwise
   */
  protected abstract boolean matchingSSODescriptorType(EntityDescriptor descriptor);

  /**
   * Assigns the entityID for the {@code EntityDescriptor}.
   * 
   * @param entityID
   *          the entityID
   * @return the builder
   */
  public T entityID(String entityID) {
    this.object().setEntityID(entityID);
    return this.getThis();
  }

  /**
   * Assigns the ID attribute for the {@code EntityDescriptor}.
   * 
   * @param id
   *          the ID
   * @return the builder
   */
  public T id(String id) {
    this.object().setID(id);
    return this.getThis();
  }

  /**
   * Assigns the cacheDuration attribute for the {@code EntityDescriptor}.
   * 
   * @param cacheDuration
   *          the cache duration (in milliseconds)
   * @return the builder
   */
  public T cacheDuration(Long cacheDuration) {
    this.object().setCacheDuration(cacheDuration);
    return this.getThis();
  }

  /**
   * Assigns the valid until time.
   * 
   * @param time
   *          valid until
   * @return the builder
   */
  public T validUntil(org.joda.time.DateTime time) {
    this.object().setValidUntil(time);
    return this.getThis();
  }

  /**
   * Assigns the valid until time (using the local time zone).
   * 
   * @param time
   *          valid until
   * @return the builder
   */
  public T validUntil(java.time.LocalDateTime time) {
    if (time == null) {
      this.object().setValidUntil(null);
    }
    else {
      this.object().setValidUntil(new DateTime(time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
    }
    return this.getThis();
  }

  /**
   * Adds attributes to the {@code mdattr:EntityAttributes} element that is part of the metadata extension element.
   * 
   * @param attributes
   *          a list of attributes
   * @return the builder
   * @see #entityCategories(String...)
   */
  public T entityAttributesExtension(List<Attribute> attributes) {
    if (this.object().getExtensions() == null) {
      if (attributes == null || attributes.isEmpty()) {
        return this.getThis();
      }
      this.object().setExtensions(ObjectUtils.createSamlObject(Extensions.class));
    }
    Optional<EntityAttributes> entityAttributes = MetadataUtils.getEntityAttributes(this.object());
    if (!entityAttributes.isPresent()) {
      if (attributes == null || attributes.isEmpty()) {
        return this.getThis();
      }
      entityAttributes = Optional.of(ObjectUtils.createSamlObject(EntityAttributes.class));
      this.object().getExtensions().getUnknownXMLObjects().add(entityAttributes.get());
    }
    entityAttributes.get().getAttributes().clear();
    for (Attribute a : attributes) {
      try {
        entityAttributes.get().getAttributes().add(XMLObjectSupport.cloneXMLObject(a));
      }
      catch (MarshallingException | UnmarshallingException e) {
        throw new RuntimeException(e);
      }
    }
    return this.getThis();
  }

  /**
   * @see #entityAttributesExtension(List)
   * 
   * @param attributes
   *          attributes to add
   * @return the builder
   */
  public T entityAttributesExtension(Attribute... attributes) {
    return this.entityAttributesExtension(attributes != null ? Arrays.asList(attributes) : null);
  }

  /**
   * Adds a set of entity categories to the entity category attribute ({@code http://macedir.org/entity-category}) that
   * is part of the {@code mdattr:EntityAttributes} element that is part of the metadata extension element.
   * <p>
   * The method does not update any of the other attributes that may exist in the entity attributes extension.
   * </p>
   * 
   * @param entityCategories
   *          the entity category values that should be added
   * @return the builder
   * @see #entityAttributesExtension(List)
   */
  public T entityCategories(List<String> entityCategories) {
    Optional<EntityAttributes> entityAttributes = MetadataUtils.getEntityAttributes(this.object());
    if (!entityAttributes.isPresent()) {
      if (entityCategories == null || entityCategories.isEmpty()) {
        return this.getThis();
      }
      return this.entityAttributesExtension(Collections.singletonList(ENTITY_CATEGORY_TEMPLATE.createBuilder()
        .value(entityCategories)
        .build()));
    }
    List<Attribute> attributeList = new ArrayList<>();
    entityAttributes.get()
      .getAttributes()
      .stream()
      .filter(a -> !ENTITY_CATEGORY_ATTRIBUTE_NAME.equals(a.getName()))
      .forEach(attributeList::add);
    if (entityCategories != null) {
      attributeList.add(ENTITY_CATEGORY_TEMPLATE.createBuilder().value(entityCategories).build());
    }
    return this.entityAttributesExtension(attributeList);
  }

  /**
   * @see #entityCategories(List)
   * 
   * @param entityCategories
   *          the entity category values that should be added
   * @return the builder
   */
  public T entityCategories(String... entityCategories) {
    return this.entityCategories(entityCategories != null ? Arrays.asList(entityCategories) : null);
  }

  /**
   * Assigns the {@code mdui:UIInfo} element as an extension to the role descriptor.
   * <p>
   * If {@code null} is supplied, the extension will be removed from the template entity descriptor (if present).
   * </p>
   * 
   * @param uiInfo
   *          the {@code UIInfo} element (will be cloned before assignment)
   * @return the builder
   */
  public T uiInfoExtension(UIInfo uiInfo) {

    SSODescriptor ssoDescriptor = this.ssoDescriptor();
    if (ssoDescriptor.getExtensions() == null) {
      if (uiInfo == null) {
        return this.getThis();
      }
      ssoDescriptor.setExtensions(ObjectUtils.createSamlObject(Extensions.class));
    }
    Optional<UIInfo> previousUIInfo = MetadataUtils.getMetadataExtension(ssoDescriptor.getExtensions(), UIInfo.class);
    if (previousUIInfo.isPresent()) {
      ssoDescriptor.getExtensions().getUnknownXMLObjects().remove(previousUIInfo.get());
      if (uiInfo == null) {
        if (ssoDescriptor.getExtensions().getUnknownXMLObjects().isEmpty()) {
          ssoDescriptor.setExtensions(null);
        }
        return this.getThis();
      }
    }
    try {
      ssoDescriptor.getExtensions().getUnknownXMLObjects().add(XMLObjectSupport.cloneXMLObject(uiInfo));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this.getThis();
  }

  /**
   * Adds the key descriptor elements.
   * 
   * @param keyDescriptors
   *          the key descriptors
   * @return the builder
   */
  public T keyDescriptors(List<KeyDescriptor> keyDescriptors) {
    SSODescriptor ssoDescriptor = this.ssoDescriptor();
    if (ssoDescriptor.getKeyDescriptors().isEmpty() && (keyDescriptors == null || keyDescriptors.isEmpty())) {
      return this.getThis();
    }
    ssoDescriptor.getKeyDescriptors().clear();
    for (KeyDescriptor kd : keyDescriptors) {
      ssoDescriptor.getKeyDescriptors().add(kd);
    }
    return this.getThis();
  }

  /**
   * @see #keyDescriptors(List)
   * @param keyDescriptors
   *          the key descriptors
   * @return the builder
   */
  public T keyDescriptors(KeyDescriptor... keyDescriptors) {
    return this.keyDescriptors(keyDescriptors != null ? Arrays.asList(keyDescriptors) : null);
  }

  /**
   * Assigns the {@code md:NameIDFormat} elements.
   * 
   * @param nameIDFormats
   *          the nameID format strings
   * @return the builder
   */
  public T nameIDFormats(List<String> nameIDFormats) {
    SSODescriptor ssoDescriptor = this.ssoDescriptor();
    ssoDescriptor.getNameIDFormats().clear();
    if (nameIDFormats == null) {
      return this.getThis();
    }
    for (String id : nameIDFormats) {
      NameIDFormat name = ObjectUtils.createSamlObject(NameIDFormat.class);
      name.setFormat(id);
      ssoDescriptor.getNameIDFormats().add(name);
    }
    return this.getThis();
  }

  /**
   * @see #nameIDFormats(List)
   * 
   * @param nameIDFormats
   *          the nameID format strings
   * @return the builder
   */
  public T nameIDFormats(String... nameIDFormats) {
    return this.nameIDFormats(nameIDFormats != null ? Arrays.asList(nameIDFormats) : null);
  }

  /**
   * Assigns the {@code Organization} element to the entity descriptor.
   * 
   * @param organization
   *          the organization (will be cloned before assignment)
   * @return the builder
   */
  public T organization(Organization organization) {
    try {
      this.object().setOrganization(XMLObjectSupport.cloneXMLObject(organization));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this.getThis();
  }

  /**
   * Assigns the {@code ContactPerson} elements to the entity descriptor.
   * 
   * @param contactPersons
   *          the contact person elements (will be cloned before assignment)
   * @return the builder
   */
  public T contactPersons(List<ContactPerson> contactPersons) {
    this.object().getContactPersons().clear();
    if (contactPersons != null) {
      for (ContactPerson cp : contactPersons) {
        try {
          this.object().getContactPersons().add(XMLObjectSupport.cloneXMLObject(cp));
        }
        catch (MarshallingException | UnmarshallingException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return this.getThis();
  }

  /**
   * @see #contactPersons(List)
   * 
   * @param contactPersons
   *          the contact person elements (will be cloned before assignment)
   * @return the builder
   */
  public T contactPersons(ContactPerson... contactPersons) {
    return this.contactPersons(contactPersons != null ? Arrays.asList(contactPersons) : null);
  }

}
