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
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.xml.namespace.QName;

import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.common.xml.SAMLSchemaBuilder;
import org.opensaml.saml.criterion.EntityRoleCriterion;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.RefreshableMetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilterChain;
import org.opensaml.saml.metadata.resolver.filter.impl.PredicateFilter;
import org.opensaml.saml.metadata.resolver.filter.impl.PredicateFilter.Direction;
import org.opensaml.saml.metadata.resolver.filter.impl.SchemaValidationFilter;
import org.opensaml.saml.metadata.resolver.filter.impl.SignatureValidationFilter;
import org.opensaml.saml.metadata.resolver.impl.AbstractMetadataResolver;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.CredentialResolver;
import org.opensaml.security.credential.impl.StaticCredentialResolver;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.xmlsec.keyinfo.KeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.BasicProviderKeyInfoCredentialResolver;
import org.opensaml.xmlsec.keyinfo.impl.provider.DEREncodedKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.DSAKeyValueProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.InlineX509DataProvider;
import org.opensaml.xmlsec.keyinfo.impl.provider.RSAKeyValueProvider;
import org.opensaml.xmlsec.signature.support.impl.ExplicitKeySignatureTrustEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.component.AbstractInitializableComponent;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.component.ComponentSupport;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

/**
 * Abstract base class for the {@link MetadataProvider} interface.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public abstract class AbstractMetadataProvider extends AbstractInitializableComponent implements MetadataProvider {

  /** Logging instance. */
  private Logger log = LoggerFactory.getLogger(AbstractMetadataProvider.class);

  /** Whether the metadata returned by queries must be valid. Default: true. */
  private boolean requireValidMetadata = true;

  /**
   * Whether problems during initialization should cause the provider to fail or go on without metadata. The assumption
   * being that in most cases a provider will recover at some point in the future. Default: false.
   */
  private boolean failFastInitialization = false;

  /**
   * The certificate(s) that is/are accepted as signing certificates for the downloaded metadata. If this attribute is
   * assigned the provider is configured to expect a valid signature on downloaded metadata.
   */
  private List<X509Certificate> signatureVerificationCertificates = null;

  /** Tells whether XML schema validation should be performed on downloaded metadata. Default: false. */
  private boolean performSchemaValidation = false;

  /** A list of inclusion predicates that will be applied to downloaded metadata. */
  private List<Predicate<EntityDescriptor>> inclusionPredicates = null;

  /** A list of exclusion predicates that will be applied to downloaded metadata. */
  private List<Predicate<EntityDescriptor>> exclusionPredicates = null;

  /** The downloaded metadata. */
  private XMLObject metadata;

  /** The time when the metadata was downloaded. */
  private Instant downloadTime;

  /** {@inheritDoc} */
  @Override
  public synchronized XMLObject getMetadata() {
    return this.metadata;
  }

  /** {@inheritDoc} */
  @Override
  public Element getMetadataDOM() throws MarshallingException {
    final XMLObject md = this.getMetadata();
    if (md == null) {
      return null;
    }
    if (md.getDOM() != null) {
      return md.getDOM();
    }
    return XMLObjectSupport.marshall(md);
  }

  /** {@inheritDoc} */
  @Override
  public Instant getLastUpdate() {
    if (RefreshableMetadataResolver.class.isInstance(this.getMetadataResolver())) {
      return ((RefreshableMetadataResolver) this.getMetadataResolver()).getLastUpdate();
    }
    return this.downloadTime;
  }

  /** {@inheritDoc} */
  @Override
  public void refresh() throws ResolverException {
    if (RefreshableMetadataResolver.class.isInstance(this.getMetadataResolver())) {
      ((RefreshableMetadataResolver) this.getMetadataResolver()).refresh();
    }
    else {
      log.debug("Refresh of metadata is not supported by {}", this.getClass().getName());
    }
  }

  /** {@inheritDoc} */
  @Override
  public Iterable<EntityDescriptor> iterator() {
    return new EntityDescriptorIterator(this.getMetadata());
  }

  /** {@inheritDoc} */
  @Override
  public Iterable<EntityDescriptor> iterator(final QName role) {
    return new EntityDescriptorIterator(this.getMetadata(), role);
  }

  /** {@inheritDoc} */
  @Override
  public EntityDescriptor getEntityDescriptor(final String entityID) throws ResolverException {
    final CriteriaSet criteria = new CriteriaSet();
    criteria.add(new EntityIdCriterion(entityID));
    return this.getMetadataResolver().resolveSingle(criteria);
  }

  /** {@inheritDoc} */
  @Override
  public IDPSSODescriptor getIDPSSODescriptor(final String entityID) throws ResolverException {
    final CriteriaSet criteria = new CriteriaSet();
    criteria.add(new EntityIdCriterion(entityID));
    criteria.add(new EntityRoleCriterion(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
    return Optional.ofNullable(this.getMetadataResolver().resolveSingle(criteria))
      .map(e -> e.getIDPSSODescriptor(SAMLConstants.SAML20P_NS))
      .orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public SPSSODescriptor getSPSSODescriptor(final String entityID) throws ResolverException {
    final CriteriaSet criteria = new CriteriaSet();
    criteria.add(new EntityIdCriterion(entityID));
    criteria.add(new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
    return Optional.ofNullable(this.getMetadataResolver().resolveSingle(criteria))
      .map(e -> e.getSPSSODescriptor(SAMLConstants.SAML20P_NS))
      .orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public List<EntityDescriptor> getIdentityProviders() throws ResolverException {
    final List<EntityDescriptor> list = new ArrayList<>();
    Iterable<EntityDescriptor> it = this.iterator(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
    it.forEach(list::add);
    return list;
  }

  /** {@inheritDoc} */
  @Override
  public List<EntityDescriptor> getServiceProviders() throws ResolverException {
    final List<EntityDescriptor> list = new ArrayList<>();
    Iterable<EntityDescriptor> it = this.iterator(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
    it.forEach(list::add);
    return list;
  }

  /**
   * Assigns the metadata that was downloaded.
   * 
   * @param metadata
   *          metadata object
   */
  private synchronized void setMetadata(final XMLObject metadata) {
    this.metadata = metadata;
    this.downloadTime = Instant.now();
  }

  /** {@inheritDoc} */
  @Override
  protected final void doInitialize() throws ComponentInitializationException {
    super.doInitialize();

    try {
      this.createMetadataResolver(this.requireValidMetadata, this.failFastInitialization, this.createFilter());
    }
    catch (ResolverException e) {
      throw new ComponentInitializationException(e);
    }
    this.initializeMetadataResolver();
  }

  /**
   * Creates the filter(s) that this instance should be configured with.
   * 
   * @return a metadata filter
   */
  protected MetadataFilter createFilter() {

    final List<MetadataFilter> filters = new ArrayList<>();

    // Verify signature?
    if (this.signatureVerificationCertificates != null && !this.signatureVerificationCertificates.isEmpty()) {
      final CredentialResolver credentialResolver = new StaticCredentialResolver(
        this.signatureVerificationCertificates.stream().map(c -> new BasicX509Credential(c)).collect(Collectors.toList()));
      KeyInfoCredentialResolver keyInfoResolver = new BasicProviderKeyInfoCredentialResolver(Arrays.asList(new RSAKeyValueProvider(),
        new InlineX509DataProvider(), new DSAKeyValueProvider(), new DEREncodedKeyValueProvider()));
      final ExplicitKeySignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credentialResolver, keyInfoResolver);
      filters.add(new SignatureValidationFilter(trustEngine));
    }

    // Schema validation?
    if (this.performSchemaValidation) {
      filters.add(new SchemaValidationFilter(new SAMLSchemaBuilder(SAMLSchemaBuilder.SAML1Version.SAML_11)));
    }

    // Inclusion predicates?
    if (this.inclusionPredicates != null) {
      for (Predicate<EntityDescriptor> p : this.inclusionPredicates) {
        filters.add(new PredicateFilter(Direction.INCLUDE, p));
      }
    }

    // Exclusion predicates?
    if (this.exclusionPredicates != null) {
      for (Predicate<EntityDescriptor> p : this.exclusionPredicates) {
        filters.add(new PredicateFilter(Direction.EXCLUDE, p));
      }
    }

    // Install the mandatory filter that saves downloaded metadata.
    filters.add((metadata, ctx) -> {
      setMetadata(metadata);
      return metadata;
    });

    if (filters.size() == 1) {
      return filters.get(0);
    }
    else {
      MetadataFilterChain chain = new MetadataFilterChain();
      chain.setFilters(filters);
      return chain;
    }
  }

  /** {@inheritDoc} */
  @Override
  protected void doDestroy() {
    super.doDestroy();
    this.destroyMetadataResolver();
  }

  /**
   * Creates the specific {@link MetadataResolver} instance for the provider implementation.
   * <p>
   * The {@code filter} parameter is a {@link MetadataFilter} that <b>must</b> be installed for the resolver. Any other
   * filters that should be installed by the specific instance should be placed last in a filter chain.
   * </p>
   * 
   * @param requireValidMetadata
   *          should be passed into {@link MetadataResolver#setRequireValidMetadata(boolean)}
   * @param failFastInitialization
   *          should be passed into {@link AbstractMetadataResolver#setFailFastInitialization(boolean)} (if applicable)
   * @param filter
   *          filter that must be installed for the resolver
   * @throws ResolverException
   *           for errors creating the resolver
   */
  protected abstract void createMetadataResolver(final boolean requireValidMetadata, final boolean failFastInitialization,
      final MetadataFilter filter) throws ResolverException;

  /**
   * Initializes the metadata resolver.
   * 
   * @throws ComponentInitializationException
   *           for initialization errors
   */
  protected abstract void initializeMetadataResolver() throws ComponentInitializationException;

  /**
   * Destroys the metadata resolver.
   */
  protected abstract void destroyMetadataResolver();

  /**
   * Sets whether the metadata returned by queries must be valid.
   * 
   * @param requireValidMetadata
   *          whether the metadata returned by queries must be valid
   */
  public void setRequireValidMetadata(final boolean requireValidMetadata) {
    ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    this.requireValidMetadata = requireValidMetadata;
  }

  /**
   * Sets whether problems during initialization should cause the provider to fail or go on without metadata. The
   * assumption being that in most cases a provider will recover at some point in the future.
   * 
   * @param failFast
   *          whether problems during initialization should cause the provider to fail
   */
  public void setFailFastInitialization(final boolean failFast) {
    ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    failFastInitialization = failFast;
  }

  /**
   * Assigns the certificate that is to be used when verifying the signature on downloaded metadata. If this attribute
   * is assigned the provider is configured to expect a valid signature on downloaded metadata.
   * 
   * @param signatureVerificationCertificate
   *          the certificate to assign
   */
  public void setSignatureVerificationCertificate(final X509Certificate signatureVerificationCertificate) {
    ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    this.signatureVerificationCertificates = Collections.singletonList(signatureVerificationCertificate);
  }

  /**
   * Assigns the certificates that are to be used when verifying the signature on downloaded metadata. If this attribute
   * is assigned the provider is configured to expect a valid signature on downloaded metadata.
   * <p>
   * The reason that more than one certificate may be assigned is that we want to be able to handle signing certificate
   * updates in a smooth way.
   * </p>
   * 
   * @param signatureVerificationCertificates
   *          the certificates to assign
   */
  public void setSignatureVerificationCertificates(final List<X509Certificate> signatureVerificationCertificates) {
    ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    this.signatureVerificationCertificates = signatureVerificationCertificates;
  }

  /**
   * Gets the certificate that is to be used when verifying the signature on downloaded metadata.
   * 
   * @return the certificates or null
   */
  public List<X509Certificate> getSignatureVerificationCertificates() {
    return this.signatureVerificationCertificates;
  }

  /**
   * Assigns whether XML schema validation should be performed on downloaded metadata.
   * 
   * @param performSchemaValidation
   *          whether schema validation should be performed
   */
  public void setPerformSchemaValidation(final boolean performSchemaValidation) {
    ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    this.performSchemaValidation = performSchemaValidation;
  }

  /**
   * Assigns a list of inclusion predicates that will be applied to downloaded metadata.
   * 
   * @param inclusionPredicates
   *          predicates
   * @see MetadataProviderPredicates
   */
  public void setInclusionPredicates(final List<Predicate<EntityDescriptor>> inclusionPredicates) {
    ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    this.inclusionPredicates = inclusionPredicates;
  }

  /**
   * Assigns a list of exclusion predicates that will be applied to downloaded metadata.
   * 
   * @param exclusionPredicates
   *          predicates
   * @see MetadataProviderPredicates
   */
  public void setExclusionPredicates(final List<Predicate<EntityDescriptor>> exclusionPredicates) {
    ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    this.exclusionPredicates = exclusionPredicates;
  }

  /**
   * Iterates over EntitiesDescriptor or EntityDescriptor.
   */
  protected static class EntityDescriptorIterator implements Iterator<EntityDescriptor>, Iterable<EntityDescriptor> {

    private Iterator<EntityDescriptor> iterator = null;

    public EntityDescriptorIterator(final XMLObject metadata) {
      this(metadata, null);
    }

    public EntityDescriptorIterator(final XMLObject metadata, final QName role) {
      if (metadata == null) {
        return;
      }
      if (metadata instanceof EntityDescriptor) {
        this.iterator = Collections.singletonList((EntityDescriptor) metadata).iterator();
      }
      else if (metadata instanceof EntitiesDescriptor) {
        List<EntityDescriptor> edList = setup((EntitiesDescriptor) metadata, role);
        this.iterator = edList.iterator();
      }
      else {
        throw new IllegalArgumentException("Expected EntityDescriptor or EntitiesDescriptor");
      }
    }

    private static List<EntityDescriptor> setup(final EntitiesDescriptor entitiesDescriptor, final QName role) {
      List<EntityDescriptor> edList = new ArrayList<>();
      entitiesDescriptor.getEntityDescriptors().stream().filter(filterRole(role)).forEach(edList::add);
      for (EntitiesDescriptor ed : entitiesDescriptor.getEntitiesDescriptors()) {
        edList.addAll(setup(ed, role));
      }
      return edList;
    }

    public static Predicate<EntityDescriptor> filterRole(final QName role) {
      return e -> role == null || !e.getRoleDescriptors(role).isEmpty();
    }

    @Override
    public boolean hasNext() {
      return this.iterator != null && this.iterator.hasNext();
    }

    @Override
    public EntityDescriptor next() {
      if (this.iterator != null) {
        return this.iterator.next();
      }
      throw new NoSuchElementException();
    }

    @Nonnull
    @Override
    public Iterator<EntityDescriptor> iterator() {
      return this;
    }
  }
}
