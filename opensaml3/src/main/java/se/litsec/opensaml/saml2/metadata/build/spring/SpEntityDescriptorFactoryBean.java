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

import java.io.IOException;
import java.util.List;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.Resource;

import net.shibboleth.utilities.java.support.xml.XMLParserException;
import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.saml2.metadata.build.AbstractEntityDescriptorBuilder;
import se.litsec.opensaml.saml2.metadata.build.SpEntityDescriptorBuilder;

/**
 * A Spring factory bean for creating {@link EntityDescriptor} objects for Service Provider metadata using setter
 * methods, and optionally a template object.
 * <p>
 * When a template object is used, the factory is initialized using the
 * {@link AbstractEntityDescriptorBuilder#AbstractEntityDescriptorBuilder(java.io.InputStream)} or
 * {@link AbstractEntityDescriptorBuilder#AbstractEntityDescriptorBuilder(EntityDescriptor)} constructors. The user may
 * later change, or add, any of the elements and attributes of the template object using the assignment methods.
 * </p>
 * <p>
 * Note that no Signature will be included.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see SpEntityDescriptorBuilder
 */
public class SpEntityDescriptorFactoryBean extends AbstractEntityDescriptorFactoryBean<SpEntityDescriptorBuilder> {

  /** The builder. */
  private SpEntityDescriptorBuilder builder;

  /**
   * Constructor setting up the factory with no template. This means that the entire {@code EntityDescriptor} object is
   * created from data assigned using set methods.
   * 
   * @see SpEntityDescriptorBuilder#SpEntityDescriptorBuilder()
   */
  public SpEntityDescriptorFactoryBean() {
    this.builder = new SpEntityDescriptorBuilder();
  }

  /**
   * Constructor setting up the factory with a template {@code EntityDescriptor} that is read from a resource. Users of
   * the bean may now change, add or delete (by using {@code null} values), the elements and attributes of the template
   * object using the setter methods.
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
  public SpEntityDescriptorFactoryBean(Resource resource) throws XMLParserException, UnmarshallingException, IOException {
    this.builder = new SpEntityDescriptorBuilder(resource.getInputStream());
  }

  /**
   * Constructor setting up the factory with a template {@code EntityDescriptor}. Users of the bean may now change, add
   * or delete (by using {@code null} values), the elements and attributes of the template object using the setter
   * methods.
   * 
   * @param template
   *          the template
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws MarshallingException
   *           for marshalling errors
   */
  public SpEntityDescriptorFactoryBean(EntityDescriptor template) throws UnmarshallingException, MarshallingException {
    this.builder = new SpEntityDescriptorBuilder(template);
  }

  /**
   * Assigns the {@code AuthnRequestsSigned} attribute of the {@code md:SPSSODescriptor} element.
   * 
   * @param b
   *          boolean (if {@code null}, the attribute is not set)
   * @see SpEntityDescriptorBuilder#authnRequestsSigned(Boolean)
   */
  public void setAuthnRequestsSigned(Boolean b) {
    this.builder.authnRequestsSigned(b);
  }

  /**
   * Assigns the {@code WantAssertionsSigned} attribute of the {@code md:SPSSODescriptor} element.
   * 
   * @param b
   *          whether assertions should be signed
   * @see SpEntityDescriptorBuilder#wantAssertionsSigned(Boolean)
   */
  public void setWantAssertionsSigned(Boolean b) {
    this.builder.wantAssertionsSigned(b);
  }

  /**
   * Adds one discovery response location.
   * 
   * @param location
   *          URL for discovery responses
   * @see SpEntityDescriptorBuilder#discoveryResponses(String...)
   */
  public void setDiscoveryResponse(String location) {
    this.builder.discoveryResponses(location);
  }

  /**
   * Adds discovery response locations in the given order (first string will be assigned index 1 and so on).
   * 
   * @param locations
   *          URLs for discovery responses
   * @see SpEntityDescriptorBuilder#discoveryResponses(List)
   */
  public void setDiscoveryResponses(List<String> locations) {
    this.builder.discoveryResponses(locations != null ? locations.toArray(new String[] {}) : null);
  }

  /**
   * Adds {@code md:AssertionConsumerService} elements to the {@code SPSSODescriptor}.
   * 
   * @param assertionConsumerServices
   *          assertion consumer service objects (cloned before assignment)
   * @see SpEntityDescriptorBuilder#assertionConsumerServices(List)
   */
  public void setAssertionConsumerServices(List<AssertionConsumerService> assertionConsumerServices) {
    this.builder.assertionConsumerServices(assertionConsumerServices);
  }

  /**
   * Adds one {@code md:AssertionConsumerService} element to the {@code SPSSODescriptor}.
   * 
   * @param assertionConsumerService
   *          assertion consumer service object (cloned before assignment)
   * @see SpEntityDescriptorBuilder#assertionConsumerServices(AssertionConsumerService...)
   */
  public void setAssertionConsumerService(AssertionConsumerService assertionConsumerService) {
    this.builder.assertionConsumerServices(assertionConsumerService);
  }

  /**
   * Adds {@code md:AttributeConsumingService} elements to the {@code SPSSODescriptor}.
   * 
   * @param attributeConsumingServices
   *          attribute consumer service objects (cloned before assignment)
   * @see SpEntityDescriptorBuilder#attributeConsumingServices(List)
   */
  public void setAttributeConsumingServices(List<AttributeConsumingService> attributeConsumingServices) {
    this.builder.attributeConsumingServices(attributeConsumingServices);
  }

  /**
   * Adds one {@code md:AttributeConsumingService} element to the {@code SPSSODescriptor}.
   * 
   * @param attributeConsumingService
   *          attribute consumer service object (cloned before assignment)
   * @see SpEntityDescriptorBuilder#attributeConsumingServices(AttributeConsumingService...)
   */
  public void setAttributeConsumingService(AttributeConsumingService attributeConsumingService) {
    this.builder.attributeConsumingServices(attributeConsumingService);
  }

  /** {@inheritDoc} */
  @Override
  protected SpEntityDescriptorBuilder _builder() {
    return this.builder;
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<EntityDescriptor> builder() {
    return this.builder;
  }

}
