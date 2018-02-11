/*
 * Copyright 2016-2018 Litsec AB
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilterChain;
import org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;

/**
 * A metadata provider that is constructed by assigning an OpenSAML {@link MetadataResolver} instance.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ProxyMetadataProvider extends AbstractMetadataProvider {

  /** The metadata resolver that we wrap in this class. */
  private AbstractMetadataResolver metadataResolver;

  /**
   * Constructor assigning the OpenSAML metadata resolver that this instance should proxy.
   * <p>
   * The supplied instance must extend the {@link AbstractMetadataResolver} class.
   * </p>
   * 
   * @param metadataResolver
   *          the metadata resolver to proxy
   */
  public ProxyMetadataProvider(MetadataResolver metadataResolver) {
    Validate.notNull(metadataResolver, "metadataResolver must not be null");
    Validate.isTrue(AbstractMetadataResolver.class.isInstance(metadataResolver), 
      "Supplied metadata resolver must extend AbstractMetadataResolver");
    this.metadataResolver = (AbstractMetadataResolver) metadataResolver;
  }
  
  /** {@inheritDoc} */
  @Override
  public String getID() {
    return this.metadataResolver.getId();
  }
    
  /** {@inheritDoc} */
  @Override
  public MetadataResolver getMetadataResolver() {
    return this.metadataResolver;
  }

  /** {@inheritDoc} */  
  @Override
  protected void createMetadataResolver(boolean requireValidMetadata, boolean failFastInitialization, MetadataFilter filter) {
    ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    
    this.metadataResolver.setRequireValidMetadata(requireValidMetadata);
    this.metadataResolver.setFailFastInitialization(failFastInitialization);
    MetadataFilter installedFilter = this.metadataResolver.getMetadataFilter();
    if (installedFilter == null) {
      this.metadataResolver.setMetadataFilter(filter);
    }
    else { 
      List<MetadataFilter> chain = new ArrayList<>();
      if (filter instanceof MetadataFilterChain) {
        chain.addAll(((MetadataFilterChain) installedFilter).getFilters());
      }
      else {
        chain.add(filter);
      }      
      if (installedFilter instanceof MetadataFilterChain) {
        chain.addAll(((MetadataFilterChain) installedFilter).getFilters());
      }
      else {
        chain.add(installedFilter);
      }
      MetadataFilterChain newFilter = new MetadataFilterChain();
      newFilter.setFilters(chain);
      this.metadataResolver.setMetadataFilter(newFilter);      
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
    this.metadataResolver.destroy();
  }

}
