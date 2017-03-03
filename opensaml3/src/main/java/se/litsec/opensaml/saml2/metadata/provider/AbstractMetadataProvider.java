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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;

import javax.xml.namespace.QName;

import org.joda.time.DateTime;
import org.opensaml.core.criterion.EntityIdCriterion;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.io.MarshallingException;
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
import se.litsec.opensaml.utils.ObjectUtils;
import se.litsec.opensaml.utils.PredicateWrapper;

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
   * being that in most cases a provider will recover at some point in the future. Default: true.
   */
  private boolean failFastInitialization = true;

  /**
   * The certificate that was used to sign metadata that is downloaded. If this attribute is assigned the provider is
   * configured to expect a valid signature on downloaded metadata.
   */
  private X509Certificate signatureVerificationCertificate = null;

  /** Tells whether XML schema validation should be performed on downloaded metadata. Default: false. */
  private boolean performSchemaValidation = false;

  /** A list of inclusion predicates that will be applied to downloaded metadata. */
  private List<Predicate<EntityDescriptor>> inclusionPredicates = null;

  /** A list of exclusion predicates that will be applied to downloaded metadata. */
  private List<Predicate<EntityDescriptor>> exclusionPredicates = null;

  /** The downloaded metadata. */
  private XMLObject metadata;

  /** The time when the metadata was downloaded. */
  private DateTime downloadTime;

  /** {@inheritDoc} */
  @Override
  public synchronized Optional<XMLObject> getMetadata() {
    return Optional.ofNullable(this.metadata);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<Element> getMetadataDOM() throws MarshallingException {
    Optional<XMLObject> md = this.getMetadata();
    if (!md.isPresent()) {
      return Optional.empty();
    }
    if (md.get().getDOM() != null) {
      return Optional.of(md.get().getDOM());
    }
    return Optional.of(ObjectUtils.marshall(md.get()));
  }

  /** {@inheritDoc} */
  @Override
  public Optional<DateTime> getLastUpdate() {
    if (RefreshableMetadataResolver.class.isInstance(this.getMetadataResolver())) {
      return Optional.ofNullable(((RefreshableMetadataResolver) this.getMetadataResolver()).getLastUpdate());
    }
    return Optional.ofNullable(this.downloadTime);
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
  public Iterable<EntityDescriptor> iterator(QName role) {
    return new EntityDescriptorIterator(this.getMetadata(), role);
  }

  /** {@inheritDoc} */
  @Override
  public Optional<EntityDescriptor> getEntityDescriptor(String entityID) throws ResolverException {
    CriteriaSet criteria = new CriteriaSet();
    criteria.add(new EntityIdCriterion(entityID));
    return Optional.ofNullable(this.getMetadataResolver().resolveSingle(criteria));
  }

  /** {@inheritDoc} */
  @Override
  public Optional<IDPSSODescriptor> getIDPSSODescriptor(String entityID) throws ResolverException {
    CriteriaSet criteria = new CriteriaSet();
    criteria.add(new EntityIdCriterion(entityID));
    criteria.add(new EntityRoleCriterion(IDPSSODescriptor.DEFAULT_ELEMENT_NAME));
    EntityDescriptor ed = this.getMetadataResolver().resolveSingle(criteria);
    return ed != null ? Optional.ofNullable(ed.getIDPSSODescriptor(SAMLConstants.SAML20P_NS)) : Optional.empty();
  }

  /** {@inheritDoc} */
  @Override
  public Optional<SPSSODescriptor> getSPSSODescriptor(String entityID) throws ResolverException {
    CriteriaSet criteria = new CriteriaSet();
    criteria.add(new EntityIdCriterion(entityID));
    criteria.add(new EntityRoleCriterion(SPSSODescriptor.DEFAULT_ELEMENT_NAME));
    EntityDescriptor ed = this.getMetadataResolver().resolveSingle(criteria);
    return ed != null ? Optional.ofNullable(ed.getSPSSODescriptor(SAMLConstants.SAML20P_NS)) : Optional.empty();
  }

  /** {@inheritDoc} */
  @Override
  public List<EntityDescriptor> getIdentityProviders() throws ResolverException {
    List<EntityDescriptor> list = new ArrayList<EntityDescriptor>();
    Iterable<EntityDescriptor> it = this.iterator(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
    it.forEach(list::add);
    return list;
  }

  /** {@inheritDoc} */
  @Override
  public List<EntityDescriptor> getServiceProviders() throws ResolverException {
    List<EntityDescriptor> list = new ArrayList<EntityDescriptor>();
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
  private synchronized final void setMetadata(XMLObject metadata) {
    this.metadata = metadata;
    this.downloadTime = new DateTime();
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

    List<MetadataFilter> filters = new ArrayList<MetadataFilter>();

    // Verify signature?
    if (this.signatureVerificationCertificate != null) {
      CredentialResolver credentialResolver = new StaticCredentialResolver(new BasicX509Credential(this.signatureVerificationCertificate));
      KeyInfoCredentialResolver keyInfoResolver = new BasicProviderKeyInfoCredentialResolver(Arrays.asList(new RSAKeyValueProvider(),
        new InlineX509DataProvider(), new DSAKeyValueProvider(), new DEREncodedKeyValueProvider()));
      ExplicitKeySignatureTrustEngine trustEngine = new ExplicitKeySignatureTrustEngine(credentialResolver, keyInfoResolver);
      filters.add(new SignatureValidationFilter(trustEngine));
    }

    // Schema validation?
    if (this.performSchemaValidation) {
      filters.add(new SchemaValidationFilter(new SAMLSchemaBuilder(SAMLSchemaBuilder.SAML1Version.SAML_11)));
    }

    // Inclusion predicates?
    if (this.inclusionPredicates != null) {
      for (Predicate<EntityDescriptor> p : this.inclusionPredicates) {
        filters.add(new PredicateFilter(Direction.INCLUDE, PredicateWrapper.wrap(p)));
      }
    }

    // Exclusion predicates?
    if (this.exclusionPredicates != null) {
      for (Predicate<EntityDescriptor> p : this.exclusionPredicates) {
        filters.add(new PredicateFilter(Direction.EXCLUDE, PredicateWrapper.wrap(p)));
      }
    }

    // Install the mandatory filter that saves downloaded metadata.
    filters.add(new MetadataFilter() {
      @Override
      public XMLObject filter(XMLObject metadata) {
        setMetadata(metadata);
        return metadata;
      }
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
  protected abstract void createMetadataResolver(boolean requireValidMetadata, boolean failFastInitialization,
      MetadataFilter filter) throws ResolverException;

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
  public void setRequireValidMetadata(boolean requireValidMetadata) {
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
  public void setFailFastInitialization(boolean failFast) {
    ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    failFastInitialization = failFast;
  }

  /**
   * Assigns the certificate that was used to sign metadata that is downloaded. If this attribute is assigned the
   * provider is configured to expect a valid signature on downloaded metadata.
   * 
   * @param signatureVerificationCertificate
   *          the certificate to assign
   */
  public void setSignatureVerificationCertificate(X509Certificate signatureVerificationCertificate) {
    ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    this.signatureVerificationCertificate = signatureVerificationCertificate;
  }

  /**
   * Assigns whether XML schema validation should be performed on downloaded metadata.
   * 
   * @param performSchemaValidation
   *          whether schema validation should be performed
   */
  public void setPerformSchemaValidation(boolean performSchemaValidation) {
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
  public void setInclusionPredicates(List<Predicate<EntityDescriptor>> inclusionPredicates) {
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
  public void setExclusionPredicates(List<Predicate<EntityDescriptor>> exclusionPredicates) {
    ComponentSupport.ifInitializedThrowUnmodifiabledComponentException(this);
    this.exclusionPredicates = exclusionPredicates;
  }

  /**
   * Iterates over EntitiesDescriptor or EntityDescriptor.
   */
  protected static class EntityDescriptorIterator implements Iterator<EntityDescriptor>, Iterable<EntityDescriptor> {

    private Iterator<EntityDescriptor> iterator = null;

    
    public EntityDescriptorIterator(Optional<XMLObject> metadata) {
      this(metadata, null);
    }
    

    public EntityDescriptorIterator(Optional<XMLObject> metadata, QName role) {
      if (!metadata.isPresent()) {
        return;
      }
      if (metadata.get() instanceof EntityDescriptor) {
        this.iterator = Arrays.asList((EntityDescriptor) metadata.get()).iterator();
      }
      else if (metadata.get() instanceof EntitiesDescriptor) {
        List<EntityDescriptor> edList = setup((EntitiesDescriptor) metadata.get(), role);
        this.iterator = edList.iterator();
      }
      else {
        throw new IllegalArgumentException("Expected EntityDescriptor or EntitiesDescriptor");
      }
    }

    private static List<EntityDescriptor> setup(EntitiesDescriptor entitiesDescriptor, QName role) {
      List<EntityDescriptor> edList = new ArrayList<EntityDescriptor>();
      entitiesDescriptor.getEntityDescriptors().stream().filter(filterRole(role)).forEach(edList::add);
      for (EntitiesDescriptor ed : entitiesDescriptor.getEntitiesDescriptors()) {
        edList.addAll(setup(ed, role));
      }
      return edList;
    }

    public static Predicate<EntityDescriptor> filterRole(QName role) {
      return e -> role != null ? !e.getRoleDescriptors(role).isEmpty() : true;
    }

    @Override
    public boolean hasNext() {
      return this.iterator != null ? this.iterator.hasNext() : false;
    }

    @Override
    public EntityDescriptor next() {
      if (this.iterator != null) {
        return this.iterator.next();
      }
      throw new NoSuchElementException();
    }

    @Override
    public Iterator<EntityDescriptor> iterator() {
      return this;
    }
  }
}
