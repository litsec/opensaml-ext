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
