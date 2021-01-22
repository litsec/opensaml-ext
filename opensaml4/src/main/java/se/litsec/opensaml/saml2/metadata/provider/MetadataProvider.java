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

import java.time.Instant;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.component.DestructableComponent;
import net.shibboleth.utilities.java.support.component.InitializableComponent;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

/**
 * An interface that offers methods that operate on one or several metadata sources.
 * <p>
 * A {@code MetadataProvider} instance encapsulates OpenSAML:s {@link MetadataResolver} and adds easy to use methods and
 * configuration.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public interface MetadataProvider extends InitializableComponent, DestructableComponent {

  /**
   * Returns the identifier for the provider.
   * 
   * @return the identifier
   */
  String getID();

  /**
   * Returns the XML element making up the metadata for the federation. This element is either an
   * {@link EntityDescriptor} or an {@link EntitiesDescriptor}.
   * 
   * @return an XML element
   * @throws ResolverException
   *           for metadata resolving errors
   */
  XMLObject getMetadata() throws ResolverException;

  /**
   * Returns the DOM element making up the metadata for the federation.
   * 
   * @return a DOM element
   * @throws MarshallingException
   *           for XML marshalling errors
   */
  Element getMetadataDOM() throws MarshallingException;

  /**
   * Returns the time the currently available metadata was last updated.
   * 
   * @return time when the currently metadata was last updated, or null if no metadata has been
   *         successfully loaded
   */
  Instant getLastUpdate();

  /**
   * Refresh the metadata handled by the provider.
   * <p>
   * An implementation that does not support refresh should implement this operation as a no-op. Implementations that do
   * support refresh of metadata should typically be either <code>synchronized</code> or make use other locking
   * mechanisms to protect against concurrent access.
   * </p>
   * 
   * @throws ResolverException
   *           if the refresh operation was unsuccessful
   */
  void refresh() throws ResolverException;

  /**
   * Returns an iterator for all entity descriptors held by the provider.
   * 
   * @return an iterator for all entity descriptors
   */
  Iterable<EntityDescriptor> iterator();

  /**
   * Returns an iterator for all entity descriptors having the given role.
   * <p>
   * To list all IdP:s and SP:s do:
   * </p>
   * 
   * <pre>{@code 
   * idps = provider.iterator(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
   * sps = provider.iterator(SPSSODescriptor.DEFAULT_ELEMENT_NAME);}
   * </pre>
   * 
   * @param role
   *          role descriptor
   * @return an iterator for all matching entity descriptors
   * @see #getIdentityProviders()
   * @see #getServiceProviders()
   */
  Iterable<EntityDescriptor> iterator(final QName role);

  /**
   * Returns the entity descriptor identified by the given entityID.
   * 
   * @param entityID
   *          the unique entityID for the entry
   * @return an entity descriptor
   * @throws ResolverException
   *           for underlying metadata errors
   */
  EntityDescriptor getEntityDescriptor(final String entityID) throws ResolverException;

  /**
   * A utility method that extracts the IdP SSO descriptor from the Entity Descriptor identified by the supplied
   * entityID.
   * 
   * @param entityID
   *          the entityID for the IdP
   * @return the SSO descriptor for the IdP
   * @throws ResolverException
   *           for underlying metadata errors
   * @see #getEntityDescriptor(String)
   */
  IDPSSODescriptor getIDPSSODescriptor(final String entityID) throws ResolverException;

  /**
   * A utility method that extracts the SP SSO descriptor from the Entity Descriptor identified by the supplied
   * entityID.
   * 
   * @param entityID
   *          the entityID for the Service Provider
   * @return the SSO descriptor for the SP
   * @throws ResolverException
   *           for underlying metadata errors
   * @see #getEntityDescriptor(String)
   */
  SPSSODescriptor getSPSSODescriptor(final String entityID) throws ResolverException;

  /**
   * Utility method that returns a list of entity descriptors for Identity Providers found in the metadata.
   * 
   * @return a list of entity descriptors
   * @throws ResolverException
   *           for metadata errors
   */
  List<EntityDescriptor> getIdentityProviders() throws ResolverException;

  /**
   * Utility method that returns a list of entity descriptors for Service Providers found in the metadata.
   * 
   * @return a list of entity descriptors
   * @throws ResolverException
   *           for metadata errors
   */
  List<EntityDescriptor> getServiceProviders() throws ResolverException;

  /**
   * Returns the underlying OpenSAML metadata resolver.
   * 
   * @return OpenSAML metadata resolver
   */
  MetadataResolver getMetadataResolver();

}
