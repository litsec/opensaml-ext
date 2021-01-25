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
package se.litsec.opensaml.saml2.metadata.provider;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;

/**
 * A {@code MetadataProvider} that is given an object representing SAML metadata (EntityDescriptor or
 * EntitiesDescriptor).
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class StaticMetadataProvider extends AbstractMetadataProvider {

  /** The resolver. */
  private DOMMetadataResolver metadataResolver;

  /** The XML element that is the metadata. */
  private Element element;
  
  /** The identifier for the provider. */
  private String id;

  /**
   * Constructor that takes a DOM element representing the metadata.
   * 
   * @param element
   *          DOM element
   */
  public StaticMetadataProvider(final Element element) {
    this.element = element;
  }

  /**
   * Constructor that takes an {@code EntityDescriptor} object.
   * 
   * @param entityDescriptor
   *          the metadata EntityDescriptor
   * @throws MarshallingException
   *           if the supplied object cannot be marshalled
   */
  public StaticMetadataProvider(final EntityDescriptor entityDescriptor) throws MarshallingException {
    this.element = entityDescriptor.getDOM();
    if (this.element == null) {
      this.element = XMLObjectSupport.marshall(entityDescriptor);
    }
  }

  /**
   * Constructor that takes an {@code EntitiesDescriptor} object.
   * 
   * @param entitiesDescriptor
   *          the metadata EntitiesDescriptor
   * @throws MarshallingException
   *           if the supplied object cannot be marshalled
   */
  public StaticMetadataProvider(final EntitiesDescriptor entitiesDescriptor) throws MarshallingException {
    this.element = entitiesDescriptor.getDOM();
    if (this.element == null) {
      this.element = XMLObjectSupport.marshall(entitiesDescriptor);
    }
  }
  
  /** {@inheritDoc} */
  @Override
  public String getID() {
    if (this.id == null) {
      this.id = this.element.getAttribute("ID");
      if (this.id == null) {
        this.id = this.toString();
      }
    }
    return this.id;
  }

  /** {@inheritDoc} */
  @Override
  public MetadataResolver getMetadataResolver() {
    return this.metadataResolver;
  }

  /** {@inheritDoc} */
  @Override
  protected void createMetadataResolver(final boolean requireValidMetadata, final boolean failFastInitialization, 
      final MetadataFilter filter) {
    this.metadataResolver = new DOMMetadataResolver(this.element);
    this.metadataResolver.setRequireValidMetadata(requireValidMetadata);
    this.metadataResolver.setFailFastInitialization(failFastInitialization);
    this.metadataResolver.setMetadataFilter(filter);
    this.metadataResolver.setId(this.getID());
  }

  /** {@inheritDoc} */
  @Override
  protected void initializeMetadataResolver() throws ComponentInitializationException {
    this.metadataResolver.initialize();
  }

  /** {@inheritDoc} */
  @Override
  protected void destroyMetadataResolver() {
    if (this.metadataResolver != null) {
      this.metadataResolver.destroy();
    }
  }

}
