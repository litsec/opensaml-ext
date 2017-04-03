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

  /** The keystore holding the certificates that we trust for TLS connections. */
  private KeyStore tlsTrustStore;

  /**
   * Creates a provider that peiodically downloads data from the URL given by {@code metadataUrl}. If the
   * {@code backupFile} parameter is given the provider also stores the downloaded metadata on disk as backup.
   * <p>
   * This constructor will initialize the underlying {@code MetadataResolver} with a default {@code HttpClient} instance
   * that is initialized according to {@link #createDefaultHttpClient()}.
   * </p>
   * <p>
   * Since to trust store for TLS connections is given, this will be read from the system properties
   * {@code javax.net.ssl.trustStore} and {@code javax.net.ssl.trustStorePassword}.
   * </p>
   * 
   * @param metadataUrl
   *          the URL to use when downloading metadata
   * @param backupFile
   *          optional path to the file to where the provider should store downloaded metadata
   * @throws ResolverException
   *           if the supplied metadata URL is invalid
   * @see #HTTPMetadataProvider(String, String, HttpClient)
   */
  public HTTPMetadataProvider(String metadataUrl, String backupFile) throws ResolverException {
    this(metadataUrl, backupFile, createDefaultHttpClient(), null);
  }

  /**
   * Creates a provider that peiodically downloads data from the URL given by {@code metadataUrl}. If the
   * {@code backupFile} parameter is given the provider also stores the downloaded metadata on disk as backup.
   * <p>
   * This constructor will initialize the underlying {@code MetadataResolver} with a default {@code HttpClient} instance
   * that is initialized according to {@link #createDefaultHttpClient()}.
   * </p>
   * 
   * @param metadataUrl
   *          the URL to use when downloading metadata
   * @param backupFile
   *          optional path to the file to where the provider should store downloaded metadata
   * @param tlsTrustStore
   *          a keystore holding the certificates to trust for TLS connections (if not set, only HTTP connections will
   *          be possible)
   * @throws ResolverException
   *           if the supplied metadata URL is invalid
   * @see #HTTPMetadataProvider(String, String, HttpClient)
   */
  public HTTPMetadataProvider(String metadataUrl, String backupFile, KeyStore tlsTrustStore) throws ResolverException {
    this(metadataUrl, backupFile, createDefaultHttpClient(), tlsTrustStore);
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
   * @param tlsTrustStore
   *          a keystore holding the certificates to trust for TLS connections (if not set, only HTTP connections will
   *          be possible)
   * @throws ResolverException
   *           if the supplied metadata URL is invalid
   */
  public HTTPMetadataProvider(String metadataUrl, String backupFile, HttpClient httpClient, KeyStore tlsTrustStore)
      throws ResolverException {
    Validate.notEmpty(metadataUrl, "metadataUrl must be set");
    Validate.notNull(httpClient, "httpClient must not be null");

    this.metadataResolver = backupFile != null
        ? new FileBackedHTTPMetadataResolver(httpClient, metadataUrl, backupFile)
        : new HTTPMetadataResolver(httpClient, metadataUrl);
        
    if (tlsTrustStore == null) {
      log.info("Loading TLS trust store from system properties ...");
      try {
        this.tlsTrustStore = KeyStoreUtils.loadSystemTrustStore();
      }
      catch (KeyStoreException e) {
        log.error("Failed to load system trust store", e);
        throw new ResolverException("Failed to load system trust store", e);
      }
    }
    else {
      this.tlsTrustStore = tlsTrustStore;
    }
  }

  /**
   * Creates a {@link HttpClient} instance that uses system properties and sets a SSLSocketFactory according to:
   * 
   * <pre>
   * new TrustEngineTLSSocketFactory(
   *   HttpClientSupport.buildNoTrustSSLConnectionSocketFactory(), new StrictHostnameVerifier());
   * </pre>
   * 
   * @return a default {@code HttpClient} instance
   */
  public static HttpClient createDefaultHttpClient() {

    return HttpClientBuilder
      .create()
      .useSystemProperties()
      .setSSLSocketFactory(
        new SecurityEnhancedTLSSocketFactory(
          HttpClientSupport.buildNoTrustSSLConnectionSocketFactory(), new StrictHostnameVerifier()))
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
    try {
      this.metadataResolver.setId(this.getID());
      this.metadataResolver.setFailFastInitialization(failFastInitialization);
      this.metadataResolver.setRequireValidMetadata(requireValidMetadata);
      this.metadataResolver.setParserPool(XMLObjectProviderRegistrySupport.getParserPool());
      this.metadataResolver.setMetadataFilter(filter);

      // Setup TLS trust engine.
      if (this.tlsTrustStore != null) {
        this.metadataResolver.setTLSTrustEngine(this.createTlsTrustEngine());
      }
      else {
        log.info("No TLS trust store supplied. The HTTP metadata resolver will not be able to handle HTTPS connections.");
      }
    }
    catch (KeyStoreException e) {
      log.error("Error while setting up TLS trust engine", e);
      throw new ResolverException(e);
    }
  }

  /**
   * Creates a {@code TrustEngine} instance based on the trust key store that this provider was initialized with.
   * 
   * @return a {@code TrustEngine} instance
   * @throws KeyStoreException
   *           for errors reading the TLS trust key store
   */
  private TrustEngine<? super X509Credential> createTlsTrustEngine() throws KeyStoreException {

    List<X509Certificate> trustedCertificates = KeyStoreUtils.getCertificateEntries(this.tlsTrustStore);

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
