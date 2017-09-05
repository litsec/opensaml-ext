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
import org.opensaml.security.x509.X509Credential;

/**
 * A {@code MetadataContainer} for {@code EntityDescriptor} elements. This class is useful for an entity wishing to
 * publicize its metadata.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class EntityDescriptorContainer extends AbstractMetadataContainer<EntityDescriptor> {

  /**
   * Constructor assigning the encapsulated descriptor element.
   * 
   * @param descriptor
   *          the descriptor object
   * @param signatureCredentials
   *          the signature credentials for signing the descriptor. May be {@code null}, but then no signing will be
   *          possible
   */
  public EntityDescriptorContainer(EntityDescriptor descriptor, X509Credential signatureCredentials) {
    super(descriptor, signatureCredentials);
  }

  /** {@inheritDoc} */
  @Override
  protected String getID(EntityDescriptor descriptor) {
    return descriptor.getID();
  }

  /** {@inheritDoc} */
  @Override
  protected void assignID(EntityDescriptor descriptor, String id) {
    descriptor.setID(id);
  }

  /**
   * Returns the entityID attribute.
   */
  @Override
  protected String getLogString(EntityDescriptor descriptor) {
    return descriptor.getEntityID();
  }

}
