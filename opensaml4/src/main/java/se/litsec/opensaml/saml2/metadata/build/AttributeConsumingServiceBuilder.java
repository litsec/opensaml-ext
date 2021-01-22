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
   * @param flag
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
      ServiceName serviceName = (ServiceName) XMLObjectSupport.buildXMLObject(ServiceName.DEFAULT_ELEMENT_NAME);
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
      ServiceDescription serviceDescription = (ServiceDescription) XMLObjectSupport.buildXMLObject(ServiceDescription.DEFAULT_ELEMENT_NAME);
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
    this.object().getRequestedAttributes().clear();
    if (attributes == null) {
      return null;
    }
    for (RequestedAttribute attribute : attributes) {
      try {
        this.object().getRequestedAttributes().add(XMLObjectSupport.cloneXMLObject(attribute));
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
