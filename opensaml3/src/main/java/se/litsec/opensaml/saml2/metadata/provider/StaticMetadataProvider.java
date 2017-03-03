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
package se.litsec.opensaml.saml2.metadata.provider;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.impl.DOMMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import se.litsec.opensaml.utils.ObjectUtils;

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
  public StaticMetadataProvider(Element element) {
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
  public StaticMetadataProvider(EntityDescriptor entityDescriptor) throws MarshallingException {
    this.element = entityDescriptor.getDOM();
    if (this.element == null) {
      this.element = ObjectUtils.marshall(entityDescriptor);
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
  public StaticMetadataProvider(EntitiesDescriptor entitiesDescriptor) throws MarshallingException {
    this.element = entitiesDescriptor.getDOM();
    if (this.element == null) {
      this.element = ObjectUtils.marshall(entitiesDescriptor);
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
  protected void createMetadataResolver(boolean requireValidMetadata, boolean failFastInitialization, MetadataFilter filter) {
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
