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
package se.litsec.opensaml.saml2.metadata.provider.spring;

import java.io.IOException;

import org.springframework.core.io.Resource;

import se.litsec.opensaml.saml2.metadata.provider.FilesystemMetadataProvider;

/**
 * Utility class that accepts a Spring Framework {@link org.springframework.core.io.Resource} as the metadata source.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SpringResourceMetadataProvider extends FilesystemMetadataProvider {

  /**
   * Constructor taking a Spring Framework {@link org.springframework.core.io.Resource} as the metadata source.
   * 
   * @param metadataResource
   *          the metadata source
   * @throws IOException
   *           if the given resource can not be represented as a {@code File} object
   */
  public SpringResourceMetadataProvider(Resource metadataResource) throws IOException {
    super(metadataResource.getFile());
  }

}
