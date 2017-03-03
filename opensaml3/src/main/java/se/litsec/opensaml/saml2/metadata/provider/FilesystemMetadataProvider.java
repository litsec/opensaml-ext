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

import java.io.File;

import org.apache.commons.lang.Validate;
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
  public FilesystemMetadataProvider(File metadataFile) {
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
  protected void createMetadataResolver(boolean requireValidMetadata, boolean failFastInitialization, MetadataFilter filter) throws ResolverException {
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
