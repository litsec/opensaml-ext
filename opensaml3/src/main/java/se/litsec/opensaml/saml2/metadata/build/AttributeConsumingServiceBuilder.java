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

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.metadata.AttributeConsumingService;
import org.opensaml.saml.saml2.metadata.RequestedAttribute;
import org.opensaml.saml.saml2.metadata.ServiceDescription;
import org.opensaml.saml.saml2.metadata.ServiceName;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.LocalizedString;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Builder for {@code md:AttributeConsumingService} elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AttributeConsumingServiceBuilder extends AbstractSAMLObjectBuilder<AttributeConsumingService> {

  /**
   * Utility method that creates a builder.
   * 
   * @return a builder
   */
  public static AttributeConsumingServiceBuilder builder() {
    return new AttributeConsumingServiceBuilder();
  }

  /**
   * Assigns the {@code Index} attribute.
   * 
   * @param index
   *          the index
   * @return the builder
   */
  public AttributeConsumingServiceBuilder index(Integer index) {
    this.object().setIndex(index);
    return this;
  }

  /**
   * Sets the {@code isDefault} attribute of the service.
   * 
   * @param def
   *          the Boolean
   * @return the builder
   */
  public AttributeConsumingServiceBuilder isDefault(Boolean flag) {
    this.object().setIsDefault(flag);
    return this;
  }

  /**
   * Assigns the service names.
   * 
   * @param names
   *          the service names
   * @return the builder.
   */
  public AttributeConsumingServiceBuilder serviceNames(List<LocalizedString> names) {
    this.object().getNames().clear();
    if (names == null) {
      return this;
    }
    for (LocalizedString name : names) {
      ServiceName serviceName = ObjectUtils.createSamlObject(ServiceName.class);
      serviceName.setValue(name.getLocalString());
      serviceName.setXMLLang(name.getLanguage());
      this.object().getNames().add(serviceName);
    }
    return this;
  }

  /**
   * @see #serviceNames(List)
   * 
   * @param names
   *          the service names
   * @return the builder.
   */
  public AttributeConsumingServiceBuilder serviceNames(LocalizedString... names) {
    return this.serviceNames(names != null ? Arrays.asList(names) : null);
  }

  /**
   * Assigns the descriptions.
   * 
   * @param descriptions
   *          the descriptions
   * @return the builder
   */
  public AttributeConsumingServiceBuilder descriptions(List<LocalizedString> descriptions) {
    this.object().getDescriptions().clear();
    if (descriptions == null) {
      return this;
    }
    for (LocalizedString description : descriptions) {
      ServiceDescription serviceDescription = ObjectUtils.createSamlObject(ServiceDescription.class);
      serviceDescription.setValue(description.getLocalString());
      serviceDescription.setXMLLang(description.getLanguage());
      this.object().getDescriptions().add(serviceDescription);
    }
    return this;
  }

  /**
   * @see #descriptions(List)
   * 
   * @param descriptions
   *          the descriptions
   * @return the builder
   */
  public AttributeConsumingServiceBuilder descriptions(LocalizedString... descriptions) {
    return this.descriptions(descriptions != null ? Arrays.asList(descriptions) : null);
  }

  /**
   * Assigns the {@code md:RequestedAttribute} elements.
   * 
   * @param attributes
   *          the requested attributes
   * @return the builder
   */
  public AttributeConsumingServiceBuilder requestedAttributes(List<RequestedAttribute> attributes) {
    this.object().getRequestAttributes().clear();
    if (attributes == null) {
      return null;
    }
    for (RequestedAttribute attribute : attributes) {
      try {
        this.object().getRequestAttributes().add(XMLObjectSupport.cloneXMLObject(attribute));
      }
      catch (MarshallingException | UnmarshallingException e) {
        throw new RuntimeException(e);
      }
    }
    return this;
  }

  /**
   * @see #requestedAttributes(List)
   * 
   * @param attributes
   *          the requested attributes
   * @return the builder
   */  
  public AttributeConsumingServiceBuilder requestedAttributes(RequestedAttribute... attributes) {
    return this.requestedAttributes(attributes != null ? Arrays.asList(attributes) : null);
  }

  /** {@inheritDoc} */
  @Override
  protected Class<AttributeConsumingService> getObjectType() {
    return AttributeConsumingService.class;
  }

}
