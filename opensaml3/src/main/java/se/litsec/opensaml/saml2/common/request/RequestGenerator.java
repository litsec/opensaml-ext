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
package se.litsec.opensaml.saml2.common.request;

import org.opensaml.saml.saml2.core.RequestAbstractType;

import se.litsec.opensaml.saml2.metadata.PeerMetadataResolver;

/**
 * Base interface for a SAML request generator.
 * 
 * @param <T>
 *          the request type
 * @param <I>
 *          the type of the input required by this generator
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public interface RequestGenerator<T extends RequestAbstractType, I extends RequestGeneratorInput> {

  /**
   * Generates a SAML request message.
   * 
   * @param input
   *          the request input
   * @param metadataResolver
   *          resolver for finding the metadata for the receiving entity
   * @return a request object
   * @throws RequestGenerationException
   *           for errors during request generation
   */
  RequestHttpObject<T> generateRequest(I input, PeerMetadataResolver metadataResolver) throws RequestGenerationException;

  /**
   * Returns the entityID for the Service Provider this request generator is serving.
   * 
   * @return the entityID
   */
  String getEntityID();

  /**
   * Returns the display name (for logging etc).
   * 
   * @return the name
   */
  String getName();

}
