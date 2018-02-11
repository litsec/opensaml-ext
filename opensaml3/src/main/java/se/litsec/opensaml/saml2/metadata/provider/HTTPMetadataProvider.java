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
package se.litsec.opensaml.saml2.metadata.provider;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.StrictHostnameVerifier;
import org.apache.http.impl.client.HttpClientBuilder;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.impl.FileBackedHTTPMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.HTTPMetadataResolver;
import org.opensaml.security.httpclient.HttpClientSecurityParameters;
import org.opensaml.security.httpclient.impl.SecurityEnhancedTLSSocketFactory;
import org.opensaml.security.trust.TrustEngine;
import org.opensaml.security.x509.PKIXValidationInformation;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.security.x509.impl.BasicPKIXValidationInformation;
import org.opensaml.security.x509.impl.CertPathPKIXTrustEvaluator;
import org.opensaml.security.x509.impl.PKIXX509CredentialTrustEngine;
import org.opensaml.security.x509.impl.StaticPKIXValidationInformationResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.httpclient.HttpClientSupport;
import net.shibboleth.utilities.java.support.resolver.ResolverException;
import se.litsec.opensaml.utils.KeyStoreUtils;

/**
 * A provider that downloads metadata from a HTTP resource.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see HTTPMetadataResolver
 * @see FileBackedHTTPMetadataResolver
 */
public class HTTPMetadataProvider extends AbstractMetadataProvider {

  /** Logging instance. */
  private Logger log = LoggerFactory.getLogger(HTTPMetadataProvider.class);

  /** The metadata resolver. */
  private HTTPMetadataResolver metadataResolver;

  /** TLS security parameters for the TLS connections, including the TLS truststore. */
  private HttpClientSecurityParameters tlsSecurityParameters;

  /**
   * Creates a provider that periodically downloads data from the URL given by {@code metadataUrl}. If the
   * {@code backupFile} parameter is given the provider also stores the downloaded metadata on disk as backup.
   * <p>
   * This constructor will initialize the underlying {@code MetadataResolver} with a default {@code HttpClient} instance
   * that is initialized according to {@link #createDefaultHttpClient()}.
   * </p>
   * <p>
   * Since no security parameters for TLS connections are given, this will be read from the system properties
   * {@code javax.net.ssl.trustStore} and {@code javax.net.ssl.trustStorePassword}.
   * </p>
   * 
   * @param metadataUrl
   *          the URL to use when downloading metadata
   * @param backupFile
   *          optional path to the file to where the provider should store downloaded metadata
   * @throws ResolverException
   *           if the supplied metadata URL is invalid
   */
  public HTTPMetadataProvider(String metadataUrl, String backupFile) throws ResolverException {
    this(metadataUrl, backupFile, createDefaultHttpClient(), null);
  }

  /**
   * Creates a provider that peiodically downloads data from the URL given by {@code metadataUrl}. If the
   * {@code backupFile} parameter is given the provider also stores the downloaded metadata on disk as backup.
   * <p>
   * This constructor will initialize the underlying {@code MetadataResolver} with a default {@code HttpClient} instance
   * that is initialized according to {@link #createDefaultHttpClient()} and the supplied security parameters.
   * </p>
   * 
   * @param metadataUrl
   *          the URL to use when downloading metadata
   * @param backupFile
   *          optional path to the file to where the provider should store downloaded metadata
   * @param tlsSecurityParameters
   *          security parameters to use for TLS connections (including TLS truststore). If not set, default system
   *          settings will be applied
   * @throws ResolverException
   *           if the supplied metadata URL is invalid
   */
  public HTTPMetadataProvider(String metadataUrl, String backupFile, HttpClientSecurityParameters tlsSecurityParameters)
      throws ResolverException {
    this(metadataUrl, backupFile, createDefaultHttpClient(), tlsSecurityParameters);
  }

  /**
   * Creates a provider that peiodically downloads data from the URL given by {@code metadataUrl}. If the
   * {@code backupFile} parameter is given the provider also stores the downloaded metadata on disk as backup.
   * 
   * @param metadataUrl
   *          the URL to use when downloading metadata
   * @param backupFile
   *          optional path to the file to where the provider should store downloaded metadata
   * @param httpClient
   *          the {@code HttpClient} that should be used to download the metadata
   * @param tlsSecurityParameters
   *          security parameters to use for TLS connections (including TLS truststore). If not set, default system
   *          settings will be applied
   * @throws ResolverException
   *           if the supplied metadata URL is invalid
   */
  public HTTPMetadataProvider(String metadataUrl, String backupFile, HttpClient httpClient,
      HttpClientSecurityParameters tlsSecurityParameters)
      throws ResolverException {
    Validate.notEmpty(metadataUrl, "metadataUrl must be set");
    Validate.notNull(httpClient, "httpClient must not be null");

    this.metadataResolver = backupFile != null
        ? new FileBackedHTTPMetadataResolver(httpClient, metadataUrl, backupFile)
        : new HTTPMetadataResolver(httpClient, metadataUrl);

    if (tlsSecurityParameters == null) {
      log.info("Loading TLS trust store from system properties ...");
      try {
        KeyStore trustStore = KeyStoreUtils.loadSystemTrustStore();
        this.tlsSecurityParameters = new HttpClientSecurityParameters();
        this.tlsSecurityParameters.setTLSTrustEngine(createTlsTrustEngine(trustStore));
        this.tlsSecurityParameters.setHostnameVerifier(new StrictHostnameVerifier());
      }
      catch (KeyStoreException e) {
        log.error("Failed to load system trust store", e);
        throw new ResolverException("Failed to load system trust store", e);
      }
    }
    else {
      this.tlsSecurityParameters = tlsSecurityParameters;
    }
  }

  /**
   * Creates a default {@link HttpClient} instance that uses system properties and sets a SSLSocketFactory that is
   * configured in a "no trust" mode, meaning that all peer certificates are accepted and no hostname check is made.
   * <p>
   * TLS security parameters, such as a trust engine, may later be added by assigning a configured
   * {@link HttpClientSecurityParameters} instance in the constructor.
   * </p>
   * 
   * @return a default {@code HttpClient} instance
   */
  public static HttpClient createDefaultHttpClient() {
    return HttpClientBuilder
      .create()
      .useSystemProperties()
      .setSSLSocketFactory(
        new SecurityEnhancedTLSSocketFactory(HttpClientSupport.buildNoTrustTLSSocketFactory()))
      .build();
  }

  /** {@inheritDoc} */
  @Override
  public String getID() {
    return this.metadataResolver.getMetadataURI();
  }

  /** {@inheritDoc} */
  @Override
  public MetadataResolver getMetadataResolver() {
    return this.metadataResolver;
  }

  /** {@inheritDoc} */
  @Override
  protected void createMetadataResolver(boolean requireValidMetadata, boolean failFastInitialization, MetadataFilter filter)
      throws ResolverException {

    this.metadataResolver.setId(this.getID());
    this.metadataResolver.setFailFastInitialization(failFastInitialization);
    this.metadataResolver.setRequireValidMetadata(requireValidMetadata);
    this.metadataResolver.setParserPool(XMLObjectProviderRegistrySupport.getParserPool());
    this.metadataResolver.setMetadataFilter(filter);

    this.metadataResolver.setHttpClientSecurityParameters(this.tlsSecurityParameters);
  }

  /**
   * Creates a {@code TrustEngine} instance based on the supplied trust key store.
   * 
   * @param trustStore
   *          the keystore holding the trusted certificates
   * @return a {@code TrustEngine} instance
   * @throws KeyStoreException
   *           for errors reading the TLS trust key store
   */
  public static TrustEngine<? super X509Credential> createTlsTrustEngine(KeyStore trustStore) throws KeyStoreException {

    List<X509Certificate> trustedCertificates = KeyStoreUtils.getCertificateEntries(trustStore);

    PKIXValidationInformation info = new BasicPKIXValidationInformation(trustedCertificates, null, null);
    StaticPKIXValidationInformationResolver resolver = new StaticPKIXValidationInformationResolver(Collections.singletonList(info),
      Collections.emptySet());
    return new PKIXX509CredentialTrustEngine(resolver, new CertPathPKIXTrustEvaluator(), null);
  }

  /** {@inheritDoc} */
  @Override
  protected void initializeMetadataResolver() throws ComponentInitializationException {
    this.metadataResolver.initialize();
  }

  /** {@inheritDoc} */
  @Override
  protected void destroyMetadataResolver() {
    if (this.metadataResolver != null) {
      this.metadataResolver.destroy();
    }
  }

}
