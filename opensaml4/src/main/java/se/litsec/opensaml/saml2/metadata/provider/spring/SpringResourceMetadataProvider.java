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
package se.litsec.opensaml.saml2.metadata.provider.spring;

import java.io.IOException;

import org.apache.commons.lang3.Validate;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.impl.ResourceBackedMetadataResolver;
import org.springframework.core.io.Resource;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import se.litsec.opensaml.saml2.metadata.provider.AbstractMetadataProvider;
import se.litsec.opensaml.utils.spring.ResourceProxy;

/**
 * Utility class that accepts a Spring Framework {@link org.springframework.core.io.Resource} as the metadata source.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SpringResourceMetadataProvider extends AbstractMetadataProvider {

  /** The underlying resolver. */
  private ResourceBackedMetadataResolver metadataResolver;

  /** The metadata resource. */
  private Resource metadataResource;

  /**
   * Constructor taking a Spring Framework {@link org.springframework.core.io.Resource} as the metadata source.
   * 
   * @param metadataResource
   *          the metadata source
   */
  public SpringResourceMetadataProvider(final Resource metadataResource) {
    Validate.notNull(metadataResource, "metadataResource must not be null");
    this.metadataResource = metadataResource;
  }

  /** {@inheritDoc} */
  @Override
  public String getID() {
    return this.metadataResource.getDescription();
  }

  /** {@inheritDoc} */
  @Override
  public MetadataResolver getMetadataResolver() {
    return this.metadataResolver;
  }

  /** {@inheritDoc} */
  @Override
  protected void createMetadataResolver(final boolean requireValidMetadata, final boolean failFastInitialization, 
      final MetadataFilter filter) throws ResolverException {

    try {
      this.metadataResolver = new ResourceBackedMetadataResolver(ResourceProxy.proxy(this.metadataResource));
      this.metadataResolver.setId(this.getID());
      this.metadataResolver.setRequireValidMetadata(requireValidMetadata);
      this.metadataResolver.setFailFastInitialization(failFastInitialization);
      this.metadataResolver.setMetadataFilter(filter);
      this.metadataResolver.setParserPool(XMLObjectProviderRegistrySupport.getParserPool());
    }
    catch (IOException e) {
      throw new ResolverException(e);
    }
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
