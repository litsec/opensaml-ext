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
