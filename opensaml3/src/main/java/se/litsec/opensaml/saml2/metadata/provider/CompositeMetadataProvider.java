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

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang.Validate;
import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.impl.CompositeMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import net.shibboleth.utilities.java.support.security.RandomIdentifierGenerationStrategy;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * A metadata provider that collects its metadata from multiple sources (providers).
 * <p>
 * It is recommended that all providers installed have the {@code failFastInitialization} property set to {@code false}.
 * Otherwise a failing provider will shut down the entire compostite provider.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see CompositeMetadataResolver
 */
public class CompositeMetadataProvider extends AbstractMetadataProvider {

  /** Logging instance. */
  private Logger log = LoggerFactory.getLogger(CompositeMetadataProvider.class);

  /** The metadata resolver. */
  private CompositeMetadataResolverEx metadataResolver;

  /** The list of underlying metadata providers. */
  private List<MetadataProvider> metadataProviders;

  /** The identifier for the provider. */
  private String id;

  /** The time that this provider was initialized. */
  private DateTime initTime;

  /** The downloaded metadata from all providers. */
  private EntitiesDescriptor compositeMetadata;

  /** A timestamp for when the {@code compositeMetadata} was put together. */
  private DateTime compositeMetadataCreationTime;
  
  /** Generates ID. */
  private RandomIdentifierGenerationStrategy idGenerator = new RandomIdentifierGenerationStrategy(20);

  /**
   * Constructs a composite metadata provider by assigning it a list of provider instances that it shall read its
   * metadata from.
   * <p>
   * The {@code id} parameter will also by used as the {@code Name} attribute for the {@code EntitiesDescriptor} that will be returned by {@link #getMetadata()}.
   * </p>
   * 
   * @param id
   *          the identifier for the provider (may not be changed later on)
   * @param metadataProviders
   *          a list of providers
   */
  public CompositeMetadataProvider(String id, List<MetadataProvider> metadataProviders) {
    Validate.notNull(id, "id must not be null");
    Validate.notNull(metadataProviders, "metadataProviders must not be null");
    this.id = id;
    this.metadataProviders = metadataProviders;
  }

  /** {@inheritDoc} */
  @Override
  public String getID() {
    return this.id;
  }

  /** {@inheritDoc} */
  @Override
  public MetadataResolver getMetadataResolver() {
    return this.metadataResolver;
  }

  /**
   * Collects all metadata from all underlying providers and creates an {@code EntitiesDescriptor} element. Any
   * duplicate entity ID:s will be removed.
   */
  @Override
  public synchronized Optional<XMLObject> getMetadata() {

    Optional<DateTime> lastUpdate = this.getLastUpdate();

    // Do we have any metadata?
    //
    if (!lastUpdate.isPresent()) {
      log.debug("No metadata available for provider '{}'", this.getID());
      return Optional.empty();
    }

    // Time to collect new metadata from the providers?
    //
    if (this.compositeMetadata == null || this.compositeMetadataCreationTime.isBefore(lastUpdate.get())) {
      this.collectMetadata();
      return Optional.ofNullable(this.compositeMetadata);
    }
    else {
      return Optional.of(this.compositeMetadata);
    }
  }

  /**
   * Collects metadata from all underlying providers.
   */
  private synchronized void collectMetadata() {

    log.debug("Collecting composite metadata for {} ...", this.getID());

    List<String> entityIds = new ArrayList<String>();

    EntitiesDescriptor metadata = ObjectUtils.createSamlObject(EntitiesDescriptor.class);
    metadata.setName(this.getID());
    metadata.setID("metadata_" + this.idGenerator.generateIdentifier(true));

    for (MetadataProvider provider : this.metadataProviders) {
      Iterator<EntityDescriptor> it = provider.iterator().iterator();
      while (it.hasNext()) {
        EntityDescriptor ed = it.next();
        if (entityIds.contains(ed.getEntityID())) {
          log.warn("EntityDescriptor for '{}' already exists in metadata. Entry read from provider '{}' will be ignored.", ed.getEntityID(),
            provider.getID());
          continue;
        }
        try {
          // Make a copy of the descriptor since we may want to modify it.
          EntityDescriptor edCopy = XMLObjectSupport.cloneXMLObject(ed);

          // Remove signature, cacheDuration and validity.
          edCopy.setSignature(null);
          edCopy.setCacheDuration(null);
          edCopy.setValidUntil(null);

          metadata.getEntityDescriptors().add(edCopy);
          entityIds.add(edCopy.getEntityID());
          log.trace("EntityDescriptor '{}' added to composite metadata", edCopy.getEntityID());
        }
        catch (MarshallingException | UnmarshallingException e) {
          log.error("Error copying EntityDescriptor '{}' ({}), entry will not be included in metadata");
        }
      }
    }
    this.compositeMetadataCreationTime = new DateTime(ISOChronology.getInstanceUTC());
    this.compositeMetadata = metadata;
    log.info("Composite metadata for {} collected and compiled into EntitiesDescriptor", this.getID());
  }

  /** {@inheritDoc} */
  @Override
  public Optional<DateTime> getLastUpdate() {
    DateTime lastUpdate = this.metadataResolver.getLastUpdate();
    if (lastUpdate != null) {
      return Optional.of(lastUpdate);
    }
    return Optional.ofNullable(this.initTime);
  }

  /** {@inheritDoc} */
  @Override
  protected void createMetadataResolver(boolean requireValidMetadata, boolean failFastInitialization, MetadataFilter filter)
      throws ResolverException {

    this.metadataResolver = new CompositeMetadataResolverEx();
    this.metadataResolver.setId(this.id);
    // We don't install the resolvers until initializeMetadataResolver().
  }

  /**
   * Returns {@code null} since the {@code CompositeMetadataResolver} doesn't perform any filtering.
   */
  @Override
  protected MetadataFilter createFilter() {
    return null;
  }

  /**
   * A list of provider ID:s for underlying providers that should be destroyed (by {@link #destroyMetadataResolver()}).
   * Only the providers that are initialized by this instance will be destroyed.
   */
  private List<String> destroyList = new ArrayList<String>();

  /** {@inheritDoc} */
  @Override
  protected void initializeMetadataResolver() throws ComponentInitializationException {
    log.debug("Initializing CompositeMetadataProvider ...");
    for (MetadataProvider p : this.metadataProviders) {
      String id = p.getID();
      if (p.isInitialized()) {
        log.debug("Underlying provider ({}) has already been initialized", id);
      }
      else {
        log.trace("Initializing underlying provider ({}) ...", id);
        p.initialize();
        this.destroyList.add(id);
        log.debug("Underlying provider ({}) successfully initialized", id);
      }
    }

    // OK, now we save the init time since we may used that to answer the getLastUpdate queries.
    //
    this.initTime = new DateTime(ISOChronology.getInstanceUTC());

    // At this point we know that all the underlying providers/resolvers have been initialized
    // and we can install them.
    //
    List<MetadataResolver> resolvers = this.metadataProviders.stream().map(p -> p.getMetadataResolver()).collect(Collectors.toList());
    if (resolvers.isEmpty()) {
      log.warn("No metadata sources installed for CompositeMetadataProvider '{}'", this.getID());
    }
    try {
      this.metadataResolver.setResolvers(resolvers);
    }
    catch (ResolverException e) {
      throw new ComponentInitializationException("Failed to install resolvers", e);
    }

    this.metadataResolver.initialize();
    log.debug("CompositeMetadataProvider successfully initialized");
  }

  /** {@inheritDoc} */
  @Override
  protected void destroyMetadataResolver() {
    for (MetadataProvider p : this.metadataProviders) {
      String id = p.getID();
      try {
        if (this.destroyList.contains(id) && p.isInitialized() && !p.isDestroyed()) {
          p.destroy();
        }
      }
      catch (Exception e) {
        log.error("Error while destroying underlying provider ({})", id, e);
      }
    }
    if (this.metadataResolver != null) {
      this.metadataResolver.destroy();
    }
  }

  /**
   * It is not possible to set configuration for metadata for a {@code CompositeMetadataResolver}. This should be done
   * on each of the underlying resolvers.
   */
  @Override
  public void setRequireValidMetadata(boolean requireValidMetadata) {
    throw new UnsupportedOperationException("Cannot configure 'requireValidMetadata' for a CompositeMetadataResolver");
  }

  /**
   * It is not possible to set configuration for metadata for a {@code CompositeMetadataResolver}. This should be done
   * on each of the underlying resolvers.
   */
  @Override
  public void setFailFastInitialization(boolean failFast) {
    throw new UnsupportedOperationException("Cannot configure 'failFastInitialization' for a CompositeMetadataResolver");
  }

  /**
   * It is not possible to set configuration for metadata for a {@code CompositeMetadataResolver}. This should be done
   * on each of the underlying resolvers.
   */
  @Override
  public void setInclusionPredicates(List<Predicate<EntityDescriptor>> inclusionPredicates) {
    throw new UnsupportedOperationException("Cannot configure 'inclusionPredicates' for a CompositeMetadataResolver");
  }

  /**
   * It is not possible to set configuration for metadata for a {@code CompositeMetadataResolver}. This should be done
   * on each of the underlying resolvers.
   */
  @Override
  public void setExclusionPredicates(List<Predicate<EntityDescriptor>> exclusionPredicates) {
    throw new UnsupportedOperationException("Cannot configure 'exclusionPredicates' for a CompositeMetadataResolver");
  }

  /**
   * It is not possible to set configuration for metadata for a {@code CompositeMetadataResolver}. This should be done
   * on each of the underlying resolvers.
   */
  @Override
  public void setSignatureVerificationCertificate(X509Certificate signatureVerificationCertificate) {
    throw new UnsupportedOperationException("Cannot configure 'signatureVerificationCertificate' for a CompositeMetadataResolver");
  }

  /**
   * It is not possible to set configuration for metadata for a {@code CompositeMetadataResolver}. This should be done
   * on each of the underlying resolvers.
   */
  @Override
  public void setPerformSchemaValidation(boolean performSchemaValidation) {
    throw new UnsupportedOperationException("Cannot configure 'performSchemaValidation' for a CompositeMetadataResolver");
  }

  /**
   * OpenSAML:s CompositeMetadataResolver is buggy since the ID property can not be set (it's hidden), and when the
   * resolver is initialized an exception is thrown saying the the ID must be set.
   */
  private static class CompositeMetadataResolverEx extends CompositeMetadataResolver {

    /**
     * Fixing what the OpenSAML developers missed. How did it pass the unit tests?
     */
    @Override
    public void setId(String componentId) {
      super.setId(componentId);
    }

  }

}
