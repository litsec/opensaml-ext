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

import java.io.File;

import org.apache.commons.lang3.Validate;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.impl.FilesystemMetadataResolver;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

/**
 * A metadata provider that reads its metadata from a file.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see FilesystemMetadataResolver
 */
public class FilesystemMetadataProvider extends AbstractMetadataProvider {

  /** The underlying resolver. */
  private FilesystemMetadataResolver metadataResolver;

  /** The metadata source. */
  private File metadataSource;

  /**
   * Constructor assigning the file holding the metadata.
   * 
   * @param metadataFile
   *          metadata source
   */
  public FilesystemMetadataProvider(final File metadataFile) {
    Validate.notNull(metadataFile, "metadataFile must not be null");
    this.metadataSource = metadataFile;
  }

  /** {@inheritDoc} */
  @Override
  public String getID() {
    return this.metadataSource.getName();
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
    this.metadataResolver = new FilesystemMetadataResolver(this.metadataSource);
    this.metadataResolver.setId(this.getID());
    this.metadataResolver.setRequireValidMetadata(requireValidMetadata);
    this.metadataResolver.setFailFastInitialization(failFastInitialization);
    this.metadataResolver.setMetadataFilter(filter);
    this.metadataResolver.setParserPool(XMLObjectProviderRegistrySupport.getParserPool());    
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
