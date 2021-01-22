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
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SSODescriptor;

import net.shibboleth.utilities.java.support.xml.XMLParserException;
import se.litsec.opensaml.saml2.metadata.MetadataUtils;

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

  /**
   * Utility method that creates a {@code SpEntityDescriptorBuilder} instance.
   * 
   * @return a {@code SpEntityDescriptorBuilder} instance
   */
  public static SpEntityDescriptorBuilder builder() {
    return new SpEntityDescriptorBuilder();
  }

  /**
   * Utility method that creates a {@code SpEntityDescriptorBuilder} instance from a supplied input stream.
   * 
   * @param resource
   *          the template resource
   * @return a {@code SpEntityDescriptorBuilder} instance
   * @throws IOException
   *           if the resource can not be read
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws XMLParserException
   *           for XML parsing errors
   */
  public static SpEntityDescriptorBuilder builder(InputStream resource) throws XMLParserException, UnmarshallingException, IOException {
    return new SpEntityDescriptorBuilder(resource);
  }

  /**
   * Utility method that creates a {@code SpEntityDescriptorBuilder} instance from a supplied template.
   * 
   * @param template
   *          the template
   * @return a {@code SpEntityDescriptorBuilder} instance
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws MarshallingException
   *           for marshalling errors
   */
  public static SpEntityDescriptorBuilder builder(EntityDescriptor template) throws UnmarshallingException, MarshallingException {
    return new SpEntityDescriptorBuilder(template);
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
   *          whether assertions should be signed
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
  public SpEntityDescriptorBuilder discoveryResponses(List<String> locations) {
    if (this.ssoDescriptor().getExtensions() == null) {
      if (locations == null) {
        return this;
      }
      this.ssoDescriptor().setExtensions((Extensions) XMLObjectSupport.buildXMLObject(Extensions.DEFAULT_ELEMENT_NAME));
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
        DiscoveryResponse discoveryResponse = (DiscoveryResponse) XMLObjectSupport.buildXMLObject(DiscoveryResponse.DEFAULT_ELEMENT_NAME);
        discoveryResponse.setBinding(SAMLConstants.SAML_IDP_DISCO_NS);
        discoveryResponse.setIndex(index++);
        discoveryResponse.setLocation(location);
        this.ssoDescriptor().getExtensions().getUnknownXMLObjects().add(discoveryResponse);
      }
    }
    return this;
  }

  /**
   * @see #discoveryResponses(List)
   * 
   * @param locations
   *          URLs for discovery responses
   * @return the builder
   */
  public SpEntityDescriptorBuilder discoveryResponses(String... locations) {
    return this.discoveryResponses(locations != null ? Arrays.asList(locations) : null);
  }

  /**
   * Adds {@code md:AssertionConsumerService} elements to the {@code SPSSODescriptor}.
   * 
   * @param assertionConsumerServices
   *          assertion consumer service objects (cloned before assignment)
   * @return the builder
   */
  public SpEntityDescriptorBuilder assertionConsumerServices(List<AssertionConsumerService> assertionConsumerServices) {
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
   * @see #assertionConsumerServices(List)
   * 
   * @param assertionConsumerServices
   *          assertion consumer service objects (cloned before assignment)
   * @return the builder
   */
  public SpEntityDescriptorBuilder assertionConsumerServices(AssertionConsumerService... assertionConsumerServices) {
    return this.assertionConsumerServices(assertionConsumerServices != null ? Arrays.asList(assertionConsumerServices) : null);
  }

  /**
   * Adds {@code md:AttributeConsumingService} elements to the {@code SPSSODescriptor}.
   * 
   * @param attributeConsumingServices
   *          attribute consumer service objects (cloned before assignment)
   * @return the builder
   */
  public SpEntityDescriptorBuilder attributeConsumingServices(List<AttributeConsumingService> attributeConsumingServices) {
    SPSSODescriptor spDescriptor = (SPSSODescriptor) this.ssoDescriptor();
    spDescriptor.getAttributeConsumingServices().clear();
    if (attributeConsumingServices == null) {
      return null;
    }
    for (AttributeConsumingService a : attributeConsumingServices) {
      try {
        spDescriptor.getAttributeConsumingServices().add(XMLObjectSupport.cloneXMLObject(a));
      }
      catch (MarshallingException | UnmarshallingException e) {
        throw new RuntimeException(e);
      }
    }
    return this;
  }

  /**
   * @see #assertionConsumerServices(List)
   * 
   * @param attributeConsumingServices
   *          attribute consumer service objects (cloned before assignment)
   * @return the builder
   */
  public SpEntityDescriptorBuilder attributeConsumingServices(AttributeConsumingService... attributeConsumingServices) {
    return this.attributeConsumingServices(attributeConsumingServices != null ? Arrays.asList(attributeConsumingServices) : null);
  }

  /** {@inheritDoc} */
  @Override
  protected SSODescriptor ssoDescriptor() {
    if (this.object().getSPSSODescriptor(SAMLConstants.SAML20P_NS) == null) {
      SPSSODescriptor d = (SPSSODescriptor) XMLObjectSupport.buildXMLObject(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
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
