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
package se.litsec.opensaml.saml2.metadata.build;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
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
import org.opensaml.saml.saml2.metadata.SingleLogoutService;

import net.shibboleth.utilities.java.support.xml.XMLParserException;
import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.saml2.attribute.AttributeTemplate;
import se.litsec.opensaml.saml2.metadata.MetadataUtils;

/**
 * Abstract base builder for creating {@code EntityDescriptor} objects using the builder pattern, and optionally a
 * template object.
 * <p>
 * When a template object is used, the builder is created using the
 * {@link #AbstractEntityDescriptorBuilder(InputStream)} or {@link #AbstractEntityDescriptorBuilder(EntityDescriptor)}
 * constructors. The user may later change, or add, any of the elements and attributes of the template object using the
 * assignment methods.
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
   * In order for us to be able to make chaining calls we need to return the concrete type of the builder.
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
    this.object().setCacheDuration(Duration.ofMillis(cacheDuration));
    return this.getThis();
  }

  /**
   * Assigns the valid until time.
   * 
   * @param time
   *          valid until
   * @return the builder
   */
  public T validUntil(final Instant time) {
    this.object().setValidUntil(time);
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
      this.object().setExtensions(Extensions.class.cast(XMLObjectSupport.buildXMLObject(Extensions.DEFAULT_ELEMENT_NAME)));
    }
    Optional<EntityAttributes> entityAttributes = MetadataUtils.getEntityAttributes(this.object());
    if (!entityAttributes.isPresent()) {
      if (attributes == null || attributes.isEmpty()) {
        return this.getThis();
      }
      entityAttributes = Optional.of(EntityAttributes.class.cast(XMLObjectSupport.buildXMLObject(EntityAttributes.DEFAULT_ELEMENT_NAME)));
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
      ssoDescriptor.setExtensions(Extensions.class.cast(XMLObjectSupport.buildXMLObject(Extensions.DEFAULT_ELEMENT_NAME)));
    }
    this.updateExtensions(ssoDescriptor.getExtensions(), uiInfo != null ? Arrays.asList(uiInfo) : null, UIInfo.DEFAULT_ELEMENT_NAME);
    if (ssoDescriptor.getExtensions().getUnknownXMLObjects().isEmpty()) {
      ssoDescriptor.setExtensions(null);
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
   * Adds a ordered list of {@code alg:SigningMethod} elements according to "SAML v2.0 Metadata Profile for Algorithm
   * Support Version 1.0".
   * 
   * @param addToRole
   *          whether the {@code alg:SigningMethod} elements should be added to an extension under the EntityDescriptor
   *          or under the role descriptor
   * @param signingMethods
   *          the signing methods to add
   * @return the builder
   */
  public T signingMethods(boolean addToRole, List<SigningMethod> signingMethods) {

    Extensions extensions = null;

    if (addToRole) {
      extensions = this.ssoDescriptor().getExtensions();
    }
    else {
      extensions = this.object().getExtensions();
    }

    if (extensions == null) {
      if (signingMethods == null || signingMethods.isEmpty()) {
        return this.getThis();
      }
      extensions = (Extensions) XMLObjectSupport.buildXMLObject(Extensions.DEFAULT_ELEMENT_NAME);
      if (addToRole) {
        this.ssoDescriptor().setExtensions(extensions);
      }
      else {
        this.object().setExtensions(extensions);
      }
    }
    this.updateExtensions(extensions, signingMethods, SigningMethod.DEFAULT_ELEMENT_NAME);
    if (extensions.getUnknownXMLObjects().isEmpty()) {
      if (addToRole) {
        this.ssoDescriptor().setExtensions(null);
      }
      else {
        this.object().setExtensions(null);
      }
    }
    return this.getThis();
  }

  /**
   * @see #signingMethods(boolean, List)
   * @param addToRole
   *          whether the {@code alg:SigningMethod} elements should be added to an extension under the EntityDescriptor
   *          or under the role descriptor
   * @param signingMethods
   *          the signing methods to add
   * @return the builder
   */
  public T signingMethods(boolean addToRole, SigningMethod... signingMethods) {
    return this.signingMethods(addToRole, signingMethods != null ? Arrays.asList(signingMethods) : null);
  }

  /**
   * Adds a ordered list of {@code alg:DigestMethod} elements according to "SAML v2.0 Metadata Profile for Algorithm
   * Support Version 1.0".
   * 
   * @param addToRole
   *          whether the {@code alg:DigestMethod} elements should be added to an extension under the EntityDescriptor
   *          or under the role descriptor
   * @param digestMethods
   *          the digest methods to add
   * @return the builder
   */
  public T digestMethods(boolean addToRole, List<DigestMethod> digestMethods) {

    Extensions extensions = null;

    if (addToRole) {
      extensions = this.ssoDescriptor().getExtensions();
    }
    else {
      extensions = this.object().getExtensions();
    }

    if (extensions == null) {
      if (digestMethods == null || digestMethods.isEmpty()) {
        return this.getThis();
      }
      extensions = (Extensions) XMLObjectSupport.buildXMLObject(Extensions.DEFAULT_ELEMENT_NAME);
      if (addToRole) {
        this.ssoDescriptor().setExtensions(extensions);
      }
      else {
        this.object().setExtensions(extensions);
      }
    }
    this.updateExtensions(extensions, digestMethods, DigestMethod.DEFAULT_ELEMENT_NAME);
    if (extensions.getUnknownXMLObjects().isEmpty()) {
      if (addToRole) {
        this.ssoDescriptor().setExtensions(null);
      }
      else {
        this.object().setExtensions(null);
      }
    }
    return this.getThis();
  }

  /**
   * @see #digestMethods(boolean, List)
   * @param addToRole
   *          whether the {@code alg:DigestMethod} elements should be added to an extension under the EntityDescriptor
   *          or under the role descriptor
   * @param digestMethods
   *          the digest methods to add
   * @return the builder
   */
  public T digestMethods(boolean addToRole, DigestMethod... digestMethods) {
    return this.digestMethods(addToRole, digestMethods != null ? Arrays.asList(digestMethods) : null);
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
      NameIDFormat name = (NameIDFormat) XMLObjectSupport.buildXMLObject(NameIDFormat.DEFAULT_ELEMENT_NAME);
      name.setURI(id);
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
   * Adds {@code md:SingleLogoutService} elements to the {@code SSODescriptor}.
   * 
   * @param singleLogoutServices
   *          single logout service objects (cloned before assignment)
   * @return the builder
   */
  public T singleLogoutServices(List<SingleLogoutService> singleLogoutServices) {
    SSODescriptor ssoDescriptor = this.ssoDescriptor();
    ssoDescriptor.getSingleLogoutServices().clear();
    if (singleLogoutServices == null) {
      return this.getThis();
    }
    for (SingleLogoutService slo : singleLogoutServices) {
      try {
        ssoDescriptor.getSingleLogoutServices().add(XMLObjectSupport.cloneXMLObject(slo));
      }
      catch (MarshallingException | UnmarshallingException e) {
        throw new RuntimeException(e);
      }
    }
    return this.getThis();
  }

  /**
   * @see #singleLogoutServices(List)
   * 
   * @param singleLogoutServices
   *          single logout service objects (cloned before assignment)
   * @return the builder
   */
  public T singleLogoutServices(SingleLogoutService... singleLogoutServices) {
    return this.singleLogoutServices(singleLogoutServices != null ? Arrays.asList(singleLogoutServices) : null);
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

  /**
   * Support method that updates an {@code Extensions} element with the supplied elements. It first removes any other
   * matching types before adding the new ones.
   * 
   * @param extensions
   *          the element to update.
   * @param elements
   *          the elements to add (may be {@code null} or empty for the remove-case)
   * @param elementName
   *          the QName of the types to add
   * @param <E>
   *          the extension type
   */
  protected <E extends XMLObject> void updateExtensions(Extensions extensions, List<E> elements, QName elementName) {
    List<XMLObject> previousContent = extensions.getUnknownXMLObjects(elementName);
    for (XMLObject p : previousContent) {
      extensions.getUnknownXMLObjects().remove(p);
    }
    if (elements == null || elements.isEmpty()) {
      return;
    }
    for (E elm : elements) {
      try {
        extensions.getUnknownXMLObjects().add(XMLObjectSupport.cloneXMLObject(elm));
      }
      catch (MarshallingException | UnmarshallingException e) {
        throw new RuntimeException(e);
      }
    }
  }
}
