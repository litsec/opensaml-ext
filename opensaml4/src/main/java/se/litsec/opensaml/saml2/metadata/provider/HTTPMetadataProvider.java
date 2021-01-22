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

import java.security.KeyStore;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.commons.lang3.Validate;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.metadata.resolver.MetadataResolver;
import org.opensaml.saml.metadata.resolver.filter.MetadataFilter;
import org.opensaml.saml.metadata.resolver.impl.FileBackedHTTPMetadataResolver;
import org.opensaml.saml.metadata.resolver.impl.HTTPMetadataResolver;
import org.opensaml.security.httpclient.HttpClientSecurityParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.httpclient.HttpClientBuilder;
import net.shibboleth.utilities.java.support.httpclient.HttpClientSupport;
import net.shibboleth.utilities.java.support.httpclient.TLSSocketFactoryBuilder;
import net.shibboleth.utilities.java.support.resolver.ResolverException;

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

  /**
   * Creates a provider that periodically downloads data from the URL given by {@code metadataUrl}. If the
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
   * @throws ResolverException
   *           if the supplied metadata URL is invalid
   */
  public HTTPMetadataProvider(final String metadataUrl, final String backupFile) throws ResolverException {
    this(metadataUrl, backupFile, createDefaultHttpClient());
  }

  /**
   * Creates a provider that periodically downloads data from the URL given by {@code metadataUrl}. If the
   * {@code backupFile} parameter is given the provider also stores the downloaded metadata on disk as backup.
   * 
   * @param metadataUrl
   *          the URL to use when downloading metadata
   * @param backupFile
   *          optional path to the file to where the provider should store downloaded metadata
   * @param httpClient
   *          the {@code HttpClient} that should be used to download the metadata
   * @throws ResolverException
   *           if the supplied metadata URL is invalid
   */
  public HTTPMetadataProvider(final String metadataUrl, final String backupFile, final HttpClient httpClient)
      throws ResolverException {
    Validate.notEmpty(metadataUrl, "metadataUrl must be set");
    Validate.notNull(httpClient, "httpClient must not be null");

    this.metadataResolver = backupFile != null
        ? new FileBackedHTTPMetadataResolver(httpClient, metadataUrl, backupFile)
        : new HTTPMetadataResolver(httpClient, metadataUrl);
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
   * @throws ResolverException
   *           for errors creating the client
   */
  public static HttpClient createDefaultHttpClient() throws ResolverException {
    return createDefaultHttpClient(null, new NoopHostnameVerifier());
  }

  /**
   * Creates a {@link HttpClient} instance that sets up a trust manager that accepts all certificates supplied in the
   * {@code trustKeyStore} parameter. The {@code hostnameVerifier} parameter tells which hostname verifier that should
   * be used. If not supplied, a {@link DefaultHostnameVerifier} will be used.
   * 
   * @param trustKeyStore
   *          a KeyStore holding the certificates that should be accepted (if null, all certificates are accepted)
   * @param hostnameVerifier
   *          the HostnameVerifier to use (if null a DefaultHostnameVerifier is used)
   * @return a HttpClient instance
   * @throws ResolverException
   *           for errors creating the client
   */
  public static HttpClient createDefaultHttpClient(final KeyStore trustKeyStore, final HostnameVerifier hostnameVerifier)
      throws ResolverException {

    try {
      List<TrustManager> managers = null;
      if (trustKeyStore != null) {
        final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(trustKeyStore);
        managers = Arrays.asList(trustManagerFactory.getTrustManagers());
      }
      else {
        managers = Arrays.asList(HttpClientSupport.buildNoTrustX509TrustManager());
      }

      final HostnameVerifier hnv = hostnameVerifier != null ? hostnameVerifier : new DefaultHostnameVerifier();

      HttpClientBuilder builder = new HttpClientBuilder();
      builder.setUseSystemProperties(true);
      builder.setTLSSocketFactory(new TLSSocketFactoryBuilder()
        .setHostnameVerifier(hnv)
        .setTrustManagers(managers)
        .build());

      return builder.buildClient();
    }
    catch (Exception e) {
      throw new ResolverException("Failed to initialize HttpClient", e);
    }
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
  protected void createMetadataResolver(final boolean requireValidMetadata, final boolean failFastInitialization,
      final MetadataFilter filter) throws ResolverException {

    this.metadataResolver.setId(this.getID());
    this.metadataResolver.setFailFastInitialization(failFastInitialization);
    this.metadataResolver.setRequireValidMetadata(requireValidMetadata);
    this.metadataResolver.setParserPool(XMLObjectProviderRegistrySupport.getParserPool());
    this.metadataResolver.setMetadataFilter(filter);
  }

  /** {@inheritDoc} */
  @Override
  protected void initializeMetadataResolver() throws ComponentInitializationException {
    
    final String url = this.metadataResolver.getMetadataURI();
    if (url != null && url.startsWith("http:")) {
      if (this.getSignatureVerificationCertificates() == null) {
        log.warn("Metadata is downloaded using HTTP and signature verification is not configured - metadata cannot be trusted");
      }
    }
    
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
