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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.idpdisco.DiscoveryResponse;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SSODescriptor;

import net.shibboleth.utilities.java.support.xml.XMLParserException;
import se.litsec.opensaml.saml2.metadata.MetadataUtils;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * A builder for building an {@code md:EntityDescription} (metadata) object for a Service Provider.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SpEntityDescriptorBuilder extends AbstractEntityDescriptorBuilder<SpEntityDescriptorBuilder> {

  /**
   * Constructor setting up the builder with no template. This means that the entire {@code EntityDescriptor} object is
   * created from data assigned using the builder.
   */
  public SpEntityDescriptorBuilder() {
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
  public SpEntityDescriptorBuilder(InputStream resource) throws XMLParserException, UnmarshallingException, IOException {
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
  public SpEntityDescriptorBuilder(EntityDescriptor template) throws UnmarshallingException, MarshallingException {
    super(template);
  }

  /** {@inheritDoc} */
  @Override
  protected SpEntityDescriptorBuilder getThis() {
    return this;
  }

  /**
   * Assigns the {@code AuthnRequestsSigned} attribute of the {@code md:SPSSODescriptor} element.
   * 
   * @param b
   *          boolean (if {@code null}, the attribute is not set)
   * @return the builder
   */
  public SpEntityDescriptorBuilder authnRequestsSigned(Boolean b) {
    ((SPSSODescriptor) this.ssoDescriptor()).setAuthnRequestsSigned(b);
    return this;
  }

  /**
   * Assigns the {@code WantAssertionsSigned} attribute of the {@code md:SPSSODescriptor} element.
   * 
   * @param b
   * @return the builder
   */
  public SpEntityDescriptorBuilder wantAssertionsSigned(Boolean b) {
    ((SPSSODescriptor) this.ssoDescriptor()).setWantAssertionsSigned(b);
    return this;
  }

  /**
   * Adds discovery response locations in the given order (first string will be assigned index 1 and so on).
   * 
   * @param locations
   *          URLs for discovery responses
   * @return the builder
   */
  public SpEntityDescriptorBuilder discoveryResponse(List<String> locations) {
    if (this.ssoDescriptor().getExtensions() == null) {
      if (locations == null) {
        return this;
      }
      this.ssoDescriptor().setExtensions(ObjectUtils.createSamlObject(Extensions.class));
    }
    else {
      if (!MetadataUtils.getMetadataExtensions(this.ssoDescriptor().getExtensions(), DiscoveryResponse.class).isEmpty()) {
        // Clear out all previous disco response elements.>
        List<XMLObject> save = this.ssoDescriptor()
          .getExtensions()
          .getOrderedChildren()
          .stream()
          .filter(e -> !DiscoveryResponse.class.isAssignableFrom(e.getClass()))
          .collect(Collectors.toList());
        this.ssoDescriptor().getExtensions().getOrderedChildren().clear();
        this.ssoDescriptor().getExtensions().getOrderedChildren().addAll(save);
      }
    }
    if (locations != null) {
      int index = 1;
      for (String location : locations) {
        DiscoveryResponse discoveryResponse = ObjectUtils.createSamlObject(DiscoveryResponse.class);
        discoveryResponse.setBinding(SAMLConstants.SAML_IDP_DISCO_NS);
        discoveryResponse.setIndex(index++);
        discoveryResponse.setLocation(location);
        this.ssoDescriptor().getExtensions().getUnknownXMLObjects().add(discoveryResponse);
      }
    }
    return this;
  }

  /**
   * @see #discoveryResponse(List)
   * 
   * @param locations
   *          URLs for discovery responses
   * @return the builder
   */
  public SpEntityDescriptorBuilder discoveryResponse(String... locations) {
    return this.discoveryResponse(locations != null ? Arrays.asList(locations) : null);
  }

  /**
   * Adds {@code md:AssertionConsumerService} elements to the {@code SPSSODescriptor}.
   * 
   * @param assertionConsumerServices
   *          assertion consumer service objects (cloned before assignment)
   * @return the builder
   */
  public SpEntityDescriptorBuilder assertionConsumerService(List<AssertionConsumerService> assertionConsumerServices) {
    SPSSODescriptor spDescriptor = (SPSSODescriptor) this.ssoDescriptor();
    spDescriptor.getAssertionConsumerServices().clear();
    if (assertionConsumerServices == null) {
      return this;
    }
    for (AssertionConsumerService a : assertionConsumerServices) {
      try {
        spDescriptor.getAssertionConsumerServices().add(XMLObjectSupport.cloneXMLObject(a));
      }
      catch (MarshallingException | UnmarshallingException e) {
        throw new RuntimeException(e);
      }
    }
    return this;
  }

  /**
   * @see #assertionConsumerService(List)
   * 
   * @param assertionConsumerServices
   *          assertion consumer service objects (cloned before assignment)
   * @return the builder
   */
  public SpEntityDescriptorBuilder assertionConsumerService(AssertionConsumerService... assertionConsumerServices) {
    return this.assertionConsumerService(assertionConsumerServices != null ? Arrays.asList(assertionConsumerServices) : null);
  }

  /** {@inheritDoc} */
  @Override
  protected SSODescriptor ssoDescriptor() {
    if (this.object().getSPSSODescriptor(SAMLConstants.SAML20P_NS) == null) {
      SPSSODescriptor d = ObjectUtils.createSamlObject(SPSSODescriptor.class);
      d.addSupportedProtocol(SAMLConstants.SAML20P_NS);
      this.object().getRoleDescriptors().add(d);
    }
    return this.object().getSPSSODescriptor(SAMLConstants.SAML20P_NS);
  }

  /** {@inheritDoc} */
  @Override
  protected boolean matchingSSODescriptorType(EntityDescriptor descriptor) {
    if (this.object().getRoleDescriptors().isEmpty()) {
      return true;
    }
    return this.object().getSPSSODescriptor(SAMLConstants.SAML20P_NS) != null;
  }

}
