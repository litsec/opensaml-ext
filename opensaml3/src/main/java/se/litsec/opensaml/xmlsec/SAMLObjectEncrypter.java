/*
 * Copyright 2016-2019 Litsec AB
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
package se.litsec.opensaml.xmlsec;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.criterion.RoleDescriptorCriterion;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.saml.security.impl.MetadataCredentialResolver;
import org.opensaml.saml.security.impl.SAMLMetadataEncryptionParametersResolver;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.criteria.UsageCriterion;
import org.opensaml.xmlsec.EncryptionConfiguration;
import org.opensaml.xmlsec.EncryptionParameters;
import org.opensaml.xmlsec.algorithm.AlgorithmRegistry;
import org.opensaml.xmlsec.algorithm.AlgorithmSupport;
import org.opensaml.xmlsec.config.impl.DefaultSecurityConfigurationBootstrap;
import org.opensaml.xmlsec.criterion.EncryptionConfigurationCriterion;
import org.opensaml.xmlsec.criterion.EncryptionOptionalCriterion;
import org.opensaml.xmlsec.encryption.EncryptedData;
import org.opensaml.xmlsec.encryption.support.DataEncryptionParameters;
import org.opensaml.xmlsec.encryption.support.Encrypter;
import org.opensaml.xmlsec.encryption.support.EncryptionException;
import org.opensaml.xmlsec.encryption.support.KeyEncryptionParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.logic.Constraint;
import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import se.litsec.opensaml.saml2.metadata.MetadataUtils;
import se.litsec.opensaml.saml2.metadata.provider.MetadataProvider;

/**
 * Utility class for encrypting an element for a SAML entity.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SAMLObjectEncrypter {

  /** Logger instance. */
  private Logger log = LoggerFactory.getLogger(SAMLObjectEncrypter.class);

  /** Provider for finding the peer credentials. */
  private MetadataProvider metadataProvider;

  /** Resolver for finding encryption keys and parameters from SAML metadata. */
  private SAMLMetadataEncryptionParametersResolver encryptionParameterResolver;

  /** The default encryption configuration. */
  private EncryptionConfiguration defaultEncryptionConfiguration;

  /** The encrypter to use. */
  private Encrypter encrypter = new Encrypter();

  /**
   * Sets up the object encrypter without a metadata provider. This means that the peer metadata has to be supplied in
   * calls to {@link #encrypt(XMLObject, Peer)} and {@link #encrypt(XMLObject, Peer, EncryptionConfiguration)}.
   * 
   * @throws ComponentInitializationException
   *           for init errors
   */
  public SAMLObjectEncrypter() throws ComponentInitializationException {
    this(null);
  }

  /**
   * Sets up the object encrypter with a metadata provider from where we find the peer credentials.
   * 
   * @param metadataProvider
   *          the metadata provider
   * @throws ComponentInitializationException
   *           for init errors
   */
  public SAMLObjectEncrypter(MetadataProvider metadataProvider) throws ComponentInitializationException {
    if (metadataProvider != null) {
      this.metadataProvider = metadataProvider;
    }

    this.defaultEncryptionConfiguration = ConfigurationService.get(EncryptionConfiguration.class);
    if (this.defaultEncryptionConfiguration == null) {
      this.defaultEncryptionConfiguration = DefaultSecurityConfigurationBootstrap.buildDefaultEncryptionConfiguration();
    }

    MetadataCredentialResolver credentialResolver = new MetadataCredentialResolver();
    credentialResolver.setKeyInfoCredentialResolver(DefaultSecurityConfigurationBootstrap.buildBasicInlineKeyInfoCredentialResolver());
    credentialResolver.initialize();

    this.encryptionParameterResolver = new SAMLMetadataEncryptionParametersResolver(credentialResolver);
    this.encryptionParameterResolver.setMergeMetadataRSAOAEPParametersWithConfig(true);
  }

  /**
   * Maps to {@link #encrypt(XMLObject, Peer, EncryptionConfiguration)} where the default encryption configuration is
   * supplied.
   * 
   * @param xmlObject
   *          the object to encrypt
   * @param peer
   *          the peer to whom we encrypt for
   * @return an {@code EncryptedData} object
   * @throws EncryptionException
   *           for encryption errors
   */
  public EncryptedData encrypt(XMLObject xmlObject, Peer peer) throws EncryptionException {
    return this.encrypt(xmlObject, peer, this.defaultEncryptionConfiguration);
  }

  /**
   * Encrypts the supplied XML object by locating the peer encryption credentials and using the supplied configuration.
   * 
   * @param xmlObject
   *          the object to encrypt
   * @param peer
   *          the peer to whom we encrypt for
   * @param configuration
   *          the encryption configuration
   * @return an {@code EncryptedData} object
   * @throws EncryptionException
   *           for encryption errors
   */
  public EncryptedData encrypt(XMLObject xmlObject, Peer peer, EncryptionConfiguration configuration) throws EncryptionException {
    Constraint.isNotNull(xmlObject, "xmlObject must not be null");
    Constraint.isNotNull(peer, "peer must not be null");
    if (configuration == null) {
      configuration = this.defaultEncryptionConfiguration;
    }
    // Locate the peer metadata ...
    //
    EntityDescriptor peerMetadata = this.getPeerMetadata(peer);

    // Get hold of the peer credentials ...
    //
    EncryptionParameters parameters = this.getEncryptionParameters(peerMetadata, configuration);
    if (parameters == null) {
      throw new EncryptionException(String.format("No encryption credentials found for '%s'", peer.getEntityID()));
    }

    // Let's encrypt!
    //
    DataEncryptionParameters dataEncryptionParameters = new DataEncryptionParameters();
    dataEncryptionParameters.setAlgorithm(parameters.getDataEncryptionAlgorithm());
    dataEncryptionParameters.setEncryptionCredential(parameters.getDataEncryptionCredential());
    dataEncryptionParameters.setKeyInfoGenerator(parameters.getDataKeyInfoGenerator());

    KeyEncryptionParameters kekParams = new KeyEncryptionParameters();
    kekParams.setAlgorithm(parameters.getKeyTransportEncryptionAlgorithm());
    kekParams.setEncryptionCredential(parameters.getKeyTransportEncryptionCredential());
    kekParams.setKeyInfoGenerator(parameters.getKeyTransportKeyInfoGenerator());
    kekParams.setRSAOAEPParameters(parameters.getRSAOAEPParameters());

    return this.encrypter.encryptElement(xmlObject, dataEncryptionParameters, kekParams);
  }

  /**
   * Retrives the peer metadata entry.
   * 
   * @param peer
   *          the peer metadata
   * @return the entity descriptor
   * @throws EncryptionException
   *           if no metadata is found
   */
  private EntityDescriptor getPeerMetadata(Peer peer) throws EncryptionException {
    EntityDescriptor peerMetadata = peer.getMetadata();
    if (peerMetadata != null) {
      return peerMetadata;
    }
    if (this.metadataProvider == null) {
      throw new EncryptionException("Peer metadata is not available - no metadataProvider has been configured");
    }
    try {
      return this.metadataProvider.getEntityDescriptor(peer.getEntityID())
        .orElseThrow(() -> new EncryptionException(String.format("Metadata for '%s' could not be found", peer.getMetadata())));
    }
    catch (ResolverException e) {
      throw new EncryptionException("Failed to locate peer metadata", e);
    }
  }

  /**
   * Given the peer metadata and the encryption configuration, the method method returns the encryption parameters to
   * use for encryption.
   * 
   * @param metadata
   *          the peer metadata
   * @param configuration
   *          the encryption configuration
   * @return the encryption parameters to use for encrypt, or {@code null} if no match is found
   * @throws EncryptionException
   *           for errors
   */
  private EncryptionParameters getEncryptionParameters(EntityDescriptor metadata, EncryptionConfiguration configuration)
      throws EncryptionException {

    SSODescriptor descriptor = MetadataUtils.getSSODescriptor(metadata);
    if (descriptor == null) {
      throw new EncryptionException("Bad peer metadata - no SSO descriptor available");
    }
    CriteriaSet criteriaSet = new CriteriaSet();
    criteriaSet.add(new RoleDescriptorCriterion(descriptor));
    criteriaSet.add(new UsageCriterion(UsageType.ENCRYPTION));
    criteriaSet.add(new EncryptionConfigurationCriterion(configuration));
    criteriaSet.add(new EncryptionOptionalCriterion(false));
    // KeyInfoGenerationProfileCriterion - may add later ...

    try {
      return this.encryptionParameterResolver.resolveSingle(criteriaSet);
    }
    catch (ResolverException e) {
      log.error("Error during resolve of encryption parameters", e);
      throw new EncryptionException("Error during resolve of encryption parameters", e);
    }
  }

  /**
   * The encrypter to use.
   * <p>
   * If not assigned, an instance of {@link org.opensaml.xmlsec.encryption.support.Encrypter} is used.
   * </p>
   * 
   * @param encrypter
   *          the encrypter
   */
  public void setEncrypter(Encrypter encrypter) {
    if (encrypter != null) {
      this.encrypter = encrypter;
    }
  }

  /**
   * Sets the default encryption configuration to use.
   * <p>
   * If not assigned, the {@link DefaultSecurityConfigurationBootstrap#buildDefaultEncryptionConfiguration()} will be
   * used as default.
   * </p>
   * 
   * @param encryptionConfiguration
   *          default encryption configuration
   */
  public void setDefaultEncryptionConfiguration(EncryptionConfiguration encryptionConfiguration) {
    if (encryptionConfiguration != null) {
      this.defaultEncryptionConfiguration = encryptionConfiguration;
    }
  }

  /**
   * Sets the {@link AlgorithmRegistry} instance used when resolving algorithm URIs. Defaults to the registry resolved
   * via {@link AlgorithmSupport#getGlobalAlgorithmRegistry()}.
   * 
   * @param algorithmRegistry
   *          the new algorithm registry instance
   */
  public void setAlgorithmRegistry(AlgorithmRegistry algorithmRegistry) {
    if (algorithmRegistry != null) {
      this.encryptionParameterResolver.setAlgorithmRegistry(algorithmRegistry);
    }
  }

  /**
   * Represents the peer when performing encryption.
   */
  public static class Peer {

    /** Peer SAML entityID. */
    private String entityID;

    /** Peer SAML metadata entry. */
    private EntityDescriptor metadata;

    /**
     * Constructor setting the entityID of the peer.
     * 
     * @param entityID
     *          peer entityID
     */
    public Peer(String entityID) {
      Constraint.isNotEmpty(entityID, "entityID must be set");
      this.entityID = entityID;
    }

    /**
     * Constructor setting the peer metadata.
     * 
     * @param metadata
     *          peer metadata
     */
    public Peer(EntityDescriptor metadata) {
      Constraint.isNotNull(metadata, "metadata must not be null");
      this.metadata = metadata;
      this.entityID = metadata.getEntityID();
    }

    /**
     * Gets the peer entityID.
     * 
     * @return the peer entityID
     */
    public String getEntityID() {
      return this.entityID;
    }

    /**
     * Gets the peer metadata.
     * 
     * @return the peer metadata
     */
    public EntityDescriptor getMetadata() {
      return this.metadata;
    }

  }
}
