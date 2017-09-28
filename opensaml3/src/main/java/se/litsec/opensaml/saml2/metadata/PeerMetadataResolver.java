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
