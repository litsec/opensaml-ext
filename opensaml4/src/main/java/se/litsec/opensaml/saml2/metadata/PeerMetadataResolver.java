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
package se.litsec.opensaml.saml2.metadata;

import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * Functional interface that request generators and processors use to obtain the peer metadata (most oftenly the IdP metadata).
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
@FunctionalInterface
public interface PeerMetadataResolver {

  /**
   * Returns the metadata {@code EntityDescriptor} for the given entityID.
   * 
   * @param entityID
   *          the IdP entityID
   * @return an {@code EntityDescriptor} object, or {@code null} if no metadata record can be found
   */
  EntityDescriptor getMetadata(String entityID);
}
