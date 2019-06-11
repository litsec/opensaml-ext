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
package se.litsec.opensaml.utils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.ext.saml2alg.DigestMethod;
import org.opensaml.saml.ext.saml2alg.SigningMethod;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.security.credential.Credential;
import org.opensaml.xmlsec.SecurityConfigurationSupport;
import org.opensaml.xmlsec.SignatureSigningConfiguration;
import org.opensaml.xmlsec.SignatureSigningParameters;
import org.opensaml.xmlsec.criterion.SignatureSigningConfigurationCriterion;
import org.opensaml.xmlsec.impl.BasicSignatureSigningConfiguration;
import org.opensaml.xmlsec.impl.BasicSignatureSigningParametersResolver;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.opensaml.xmlsec.signature.support.SignatureSupport;

import net.shibboleth.utilities.java.support.resolver.CriteriaSet;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import se.litsec.opensaml.saml2.metadata.MetadataUtils;

/**
 * Utility methods for signatures.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SignatureUtils {

  /**
   * A recipient of a signed message may specify the signature algorithm it prefers by including the
   * {@code <alg:SigningMethod>} element in its metadata. This method locates these elements, and if present, creates a
   * {@link SignatureSigningConfiguration} object that should be supplied to
   * {@link #sign(SignableSAMLObject, Credential, SignatureSigningConfiguration...)}.
   * 
   * @param metadata
   *          the recipient's metadata
   * @return a {@link SignatureSigningConfiguration} element, or {@code null} if no preferred signing algorithms were
   *         specified
   */
  public static SignatureSigningConfiguration getSignaturePreferences(EntityDescriptor metadata) {

    if (metadata == null) {
      return null;
    }

    List<SigningMethod> signingMethods = Collections.emptyList();
    List<DigestMethod> digestMethods = Collections.emptyList();

    // First check the extensions under the role descriptor ...
    //
    SSODescriptor descriptor = MetadataUtils.getSSODescriptor(metadata);
    if (descriptor != null) {
      signingMethods = MetadataUtils.getMetadataExtensions(descriptor.getExtensions(), SigningMethod.class);
      digestMethods = MetadataUtils.getMetadataExtensions(descriptor.getExtensions(), DigestMethod.class);
    }
    // If no extensions are specified under the role descriptor, check the entity descriptor extensions ...
    //
    if (signingMethods.isEmpty()) {
      signingMethods = MetadataUtils.getMetadataExtensions(metadata.getExtensions(), SigningMethod.class);
    }
    if (digestMethods.isEmpty()) {
      digestMethods = MetadataUtils.getMetadataExtensions(metadata.getExtensions(), DigestMethod.class);
    }

    if (signingMethods.isEmpty() && digestMethods.isEmpty()) {
      return null;
    }

    BasicSignatureSigningConfiguration config = new BasicSignatureSigningConfiguration();
    if (!signingMethods.isEmpty()) {
      // We can't handle key lengths here!
      config.setSignatureAlgorithms(
        signingMethods.stream().map(SigningMethod::getAlgorithm).collect(Collectors.toList()));
    }
    if (!digestMethods.isEmpty()) {
      config.setSignatureReferenceDigestMethods(
        digestMethods.stream().map(DigestMethod::getAlgorithm).collect(Collectors.toList()));
    }

    return config;
  }

  /**
   * Signs the supplied SAML object using the credentials.
   * 
   * @param object
   *          object to sign
   * @param signingCredentials
   *          signature credentials
   * @param <T>
   *          the object type
   * @throws SignatureException
   *           for signature creation errors
   * @deprecated Use {@link #sign(SignableSAMLObject, Credential, SignatureSigningConfiguration...)} or
   *             {@link #sign(SignableSAMLObject, Credential, SignatureSigningConfiguration, EntityDescriptor)} instead
   */
  @Deprecated
  public static <T extends SignableSAMLObject> void sign(T object, Credential signingCredentials) throws SignatureException {
    sign(object, signingCredentials, SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration());
  }

  /**
   * Signs the supplied SAML object using the supplied credentials and signature configuration and also handles the peer
   * signature requirements.
   * <p>
   * This method corresponds to:
   * {@code SignatureSigningConfiguration peerConfig = SignatureUtils.getSignaturePreferences(recipientMetadata);}
   * followed by {@code SignatureUtils.sign(object, signingCredentials, config, peerConfig);}. If no peer config is
   * found, this is not passed.
   * </p>
   * 
   * @param object
   *          object to sign
   * @param signingCredentials
   *          signature credentials
   * @param config
   *          signature configuration
   * @param recipientMetadata
   *          recipient's metadata
   * @param <T>
   *          the object type
   * @throws SignatureException
   *           for signature errors
   */
  public static <T extends SignableSAMLObject> void sign(T object, Credential signingCredentials,
      SignatureSigningConfiguration config, EntityDescriptor recipientMetadata) throws SignatureException {

    SignatureSigningConfiguration peerConfig = getSignaturePreferences(recipientMetadata);
    if (config == null) {
      config = SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration();
    }

    SignatureSigningConfiguration[] configs = new SignatureSigningConfiguration[1 + (peerConfig != null ? 1 : 0)];
    int pos = 0;
    if (peerConfig != null) {
      configs[pos++] = peerConfig;
    }
    configs[pos] = config;

    sign(object, signingCredentials, configs);
  }

  /**
   * Signs the supplied SAML object using the supplied credentials and signature configuration(s).
   * <p>
   * Note: If you have obtained the peer's prefered signature credentials, this configuration should be supplied first
   * ...
   * </p>
   * 
   * @param object
   *          object to sign
   * @param signingCredentials
   *          signature credentials
   * @param configs
   *          signature configuration
   * @param <T>
   *          the object type
   * @throws SignatureException
   *           for signature creation errors
   */
  public static <T extends SignableSAMLObject> void sign(T object, Credential signingCredentials, SignatureSigningConfiguration... configs)
      throws SignatureException {

    if (configs == null || configs.length == 0) {
      configs = new SignatureSigningConfiguration[] { SecurityConfigurationSupport.getGlobalSignatureSigningConfiguration() };
    }

    try {
      object.setSignature(null);

      BasicSignatureSigningConfiguration signatureCreds = new BasicSignatureSigningConfiguration();
      signatureCreds.setSigningCredentials(Collections.singletonList(signingCredentials));

      BasicSignatureSigningParametersResolver signatureParametersResolver = new BasicSignatureSigningParametersResolver();

      SignatureSigningConfiguration[] criteriaConfig = new SignatureSigningConfiguration[configs.length + 1];
      System.arraycopy(configs, 0, criteriaConfig, 0, configs.length);
      criteriaConfig[configs.length] = signatureCreds;
      CriteriaSet criteriaSet = new CriteriaSet(new SignatureSigningConfigurationCriterion(criteriaConfig));

      SignatureSigningParameters parameters = signatureParametersResolver.resolveSingle(criteriaSet);
      SignatureSupport.signObject(object, parameters);
    }
    catch (ResolverException | org.opensaml.security.SecurityException | MarshallingException e) {
      throw new SignatureException(e);
    }
  }

  // Hidden constructor.
  private SignatureUtils() {
  }

}
