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

import java.util.List;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
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
  Optional<XMLObject> getMetadata() throws ResolverException;

  /**
   * Returns the DOM element making up the metadata for the federation.
   * 
   * @return a DOM element
   * @throws MarshallingException
   *           for XML marshalling errors
   */
  Optional<Element> getMetadataDOM() throws MarshallingException;

  /**
   * Returns the time the currently available metadata was last updated.
   * 
   * @return time when the currently metadata was last updated, or an empty optional if no metadata has been
   *         successfully loaded
   */
  Optional<DateTime> getLastUpdate();

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
  Iterable<EntityDescriptor> iterator(QName role);

  /**
   * Returns the entity descriptor identified by the given entityID.
   * 
   * @param entityID
   *          the unique entityID for the entry
   * @return an entity descriptor
   * @throws ResolverException
   *           for underlying metadata errors
   */
  Optional<EntityDescriptor> getEntityDescriptor(String entityID) throws ResolverException;

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
  Optional<IDPSSODescriptor> getIDPSSODescriptor(String entityID) throws ResolverException;

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
  Optional<SPSSODescriptor> getSPSSODescriptor(String entityID) throws ResolverException;

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
