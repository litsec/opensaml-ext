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

import java.security.cert.X509Certificate;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.Validate;
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
import net.shibboleth.utilities.java.support.security.impl.RandomIdentifierGenerationStrategy;

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
  private Instant initTime;

  /** The downloaded metadata from all providers. */
  private EntitiesDescriptor compositeMetadata;

  /** A timestamp for when the {@code compositeMetadata} was put together. */
  private Instant compositeMetadataCreationTime;

  /** Generates ID. */
  private RandomIdentifierGenerationStrategy idGenerator = new RandomIdentifierGenerationStrategy(20);

  /**
   * Constructs a composite metadata provider by assigning it a list of provider instances that it shall read its
   * metadata from.
   * <p>
   * The {@code id} parameter will also by used as the {@code Name} attribute for the {@code EntitiesDescriptor} that
   * will be returned by {@link #getMetadata()}.
   * </p>
   * 
   * @param id
   *          the identifier for the provider (may not be changed later on)
   * @param metadataProviders
   *          a list of providers
   */
  public CompositeMetadataProvider(final String id, final List<MetadataProvider> metadataProviders) {
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
  public synchronized XMLObject getMetadata() {

    final Instant lastUpdate = this.getLastUpdate();

    // Do we have any metadata?
    //
    if (lastUpdate == null) {
      log.debug("No metadata available for provider '{}'", this.getID());
      return null;
    }

    // Time to collect new metadata from the providers?
    //
    if (this.compositeMetadata == null || this.compositeMetadataCreationTime.isBefore(lastUpdate)) {
      this.collectMetadata();
    }
    return this.compositeMetadata;
  }

  /**
   * Collects metadata from all underlying providers.
   */
  private synchronized void collectMetadata() {

    log.debug("Collecting composite metadata for {} ...", this.getID());

    final List<String> entityIds = new ArrayList<>();

    final EntitiesDescriptor metadata = (EntitiesDescriptor) XMLObjectSupport.buildXMLObject(EntitiesDescriptor.DEFAULT_ELEMENT_NAME);
    metadata.setName(this.getID());
    metadata.setID("metadata_" + this.idGenerator.generateIdentifier(true));

    for (MetadataProvider provider : this.metadataProviders) {
      Iterator<EntityDescriptor> it = provider.iterator().iterator();
      while (it.hasNext()) {
        final EntityDescriptor ed = it.next();
        if (entityIds.contains(ed.getEntityID())) {
          log.warn("EntityDescriptor for '{}' already exists in metadata. Entry read from provider '{}' will be ignored.", ed.getEntityID(),
            provider.getID());
          continue;
        }
        try {
          // Make a copy of the descriptor since we may want to modify it.
          final EntityDescriptor edCopy = XMLObjectSupport.cloneXMLObject(ed);

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
    this.compositeMetadataCreationTime = Instant.now();
    this.compositeMetadata = metadata;
    log.info("Composite metadata for {} collected and compiled into EntitiesDescriptor", this.getID());
  }

  /** {@inheritDoc} */
  @Override
  public Instant getLastUpdate() {
    return Optional.ofNullable(this.metadataResolver.getLastUpdate()).orElse(this.initTime);
  }

  /** {@inheritDoc} */
  @Override
  protected void createMetadataResolver(final boolean requireValidMetadata, final boolean failFastInitialization, final MetadataFilter filter)
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
  private List<String> destroyList = new ArrayList<>();

  /** {@inheritDoc} */
  @Override
  protected void initializeMetadataResolver() throws ComponentInitializationException {
    log.debug("Initializing CompositeMetadataProvider ...");
    for (MetadataProvider p : this.metadataProviders) {
      final String id = p.getID();
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
    this.initTime = Instant.now();

    // At this point we know that all the underlying providers/resolvers have been initialized
    // and we can install them.
    //
    final List<MetadataResolver> resolvers = this.metadataProviders
      .stream()
      .map(MetadataProvider::getMetadataResolver)
      .collect(Collectors.toList());

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
      final String id = p.getID();
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
  public void setRequireValidMetadata(final boolean requireValidMetadata) {
    throw new UnsupportedOperationException("Cannot configure 'requireValidMetadata' for a CompositeMetadataResolver");
  }

  /**
   * It is not possible to set configuration for metadata for a {@code CompositeMetadataResolver}. This should be done
   * on each of the underlying resolvers.
   */
  @Override
  public void setFailFastInitialization(final boolean failFast) {
    throw new UnsupportedOperationException("Cannot configure 'failFastInitialization' for a CompositeMetadataResolver");
  }

  /**
   * It is not possible to set configuration for metadata for a {@code CompositeMetadataResolver}. This should be done
   * on each of the underlying resolvers.
   */
  @Override
  public void setInclusionPredicates(final List<Predicate<EntityDescriptor>> inclusionPredicates) {
    throw new UnsupportedOperationException("Cannot configure 'inclusionPredicates' for a CompositeMetadataResolver");
  }

  /**
   * It is not possible to set configuration for metadata for a {@code CompositeMetadataResolver}. This should be done
   * on each of the underlying resolvers.
   */
  @Override
  public void setExclusionPredicates(final List<Predicate<EntityDescriptor>> exclusionPredicates) {
    throw new UnsupportedOperationException("Cannot configure 'exclusionPredicates' for a CompositeMetadataResolver");
  }

  /**
   * It is not possible to set configuration for metadata for a {@code CompositeMetadataResolver}. This should be done
   * on each of the underlying resolvers.
   */
  @Override
  public void setSignatureVerificationCertificate(final X509Certificate signatureVerificationCertificate) {
    throw new UnsupportedOperationException("Cannot configure 'signatureVerificationCertificate' for a CompositeMetadataResolver");
  }

  /**
   * It is not possible to set configuration for metadata for a {@code CompositeMetadataResolver}. This should be done
   * on each of the underlying resolvers.
   */
  @Override
  public void setPerformSchemaValidation(final boolean performSchemaValidation) {
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
    public void setId(final @Nonnull String componentId) {
      super.setId(componentId);
    }

  }

}
