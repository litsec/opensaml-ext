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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SSODescriptor;

import net.shibboleth.utilities.java.support.xml.XMLParserException;
import se.litsec.opensaml.saml2.attribute.AttributeTemplate;
import se.litsec.opensaml.saml2.metadata.MetadataUtils;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * A builder for building an {@code md:EntityDescription} (metadata) object for an Identity Provider.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class IdpEntityDescriptorBuilder extends AbstractEntityDescriptorBuilder<IdpEntityDescriptorBuilder> {

  /**
   * The attribute name for the assurance certification attribute stored as an attribute in the entity attributes
   * extension.
   */
  public static final String ASSURANCE_CERTIFICATION_ATTRIBUTE_NAME = "urn:oasis:names:tc:SAML:attribute:assurance-certification";

  /**
   * The attribute template for the assurance certification attribute stored as an attribute in the entity attributes
   * extension.
   */
  public static final AttributeTemplate ASSURANCE_CERTIFICATION_ATTRIBUTE_TEMPLATE = new AttributeTemplate(
    ASSURANCE_CERTIFICATION_ATTRIBUTE_NAME, null);

  /**
   * Constructor setting up the builder with no template. This means that the entire {@code EntityDescriptor} object is
   * created from data assigned using the builder.
   */
  public IdpEntityDescriptorBuilder() {
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
  public IdpEntityDescriptorBuilder(InputStream resource) throws XMLParserException, UnmarshallingException, IOException {
    super(resource);
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
  public IdpEntityDescriptorBuilder(EntityDescriptor template) throws UnmarshallingException, MarshallingException {
    super(template);
  }

  /** {@inheritDoc} */
  @Override
  protected IdpEntityDescriptorBuilder getThis() {
    return this;
  }

  /** {@inheritDoc} */
  @Override
  protected SSODescriptor ssoDescriptor() {
    if (this.object().getIDPSSODescriptor(SAMLConstants.SAML20P_NS) == null) {
      IDPSSODescriptor d = ObjectUtils.createSamlObject(IDPSSODescriptor.class);
      d.addSupportedProtocol(SAMLConstants.SAML20P_NS);
      this.object().getRoleDescriptors().add(d);
    }
    return this.object().getIDPSSODescriptor(SAMLConstants.SAML20P_NS);
  }

  /** {@inheritDoc} */
  @Override
  protected boolean matchingSSODescriptorType(EntityDescriptor descriptor) {
    if (this.object().getRoleDescriptors().isEmpty()) {
      return true;
    }
    return this.object().getIDPSSODescriptor(SAMLConstants.SAML20P_NS) != null;
  }

  /**
   * Adds a set of URIs to the assurance certification attribute
   * ({@code urn:oasis:names:tc:SAML:attribute:assurance-certification}) that is part of the
   * {@code mdattr:EntityAttributes} element that is part of the metadata extension element.
   * <p>
   * The method does not update any of the other attributes that may exist in the entity attributes extension.
   * </p>
   * 
   * @param uris
   *          the assurance URI values that should be added
   * @return the builder
   * @see #entityAttributesExtension(List)
   */
  public IdpEntityDescriptorBuilder assuranceCertification(List<String> uris) {
    Optional<EntityAttributes> entityAttributes = MetadataUtils.getEntityAttributes(this.object());
    if (!entityAttributes.isPresent()) {
      if (uris == null || uris.isEmpty()) {
        return this;
      }
      return this.entityAttributesExtension(
        Collections.singletonList(ASSURANCE_CERTIFICATION_ATTRIBUTE_TEMPLATE.createBuilder().value(uris).build()));
    }
    List<Attribute> attributeList = new ArrayList<>();
    entityAttributes.get()
      .getAttributes()
      .stream()
      .filter(a -> !ASSURANCE_CERTIFICATION_ATTRIBUTE_NAME.equals(a.getName()))
      .forEach(attributeList::add);
    if (uris != null) {
      attributeList.add(ASSURANCE_CERTIFICATION_ATTRIBUTE_TEMPLATE.createBuilder().value(uris).build());
    }
    return this.entityAttributesExtension(attributeList);
  }
  
  /**
   * @see #assuranceCertification(List)
   * 
   * @param uris
   *          the assurance URI values that should be added
   * @return the builder
   * @see #entityAttributesExtension(List)
   */  
  public IdpEntityDescriptorBuilder assuranceCertification(String... uris) {
    return this.assuranceCertification(uris != null ? Arrays.asList(uris) : null);
  }

}
