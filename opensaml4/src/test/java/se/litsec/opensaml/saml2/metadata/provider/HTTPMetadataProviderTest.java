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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.springframework.core.io.Resource;

import se.litsec.opensaml.TestWebServer;
import se.litsec.opensaml.utils.KeyStoreUtils;

/**
 * Test cases for the {@code HTTPMetadataProvider} class.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class HTTPMetadataProviderTest extends BaseMetadataProviderTest {

  /** Holds the metadata that is serviced by the web server. */
  private static MetadataResourceProvider resourceProvider = new MetadataResourceProvider();
  
  /** The TLS trust. */
  private static KeyStore trustStore;

  /** The web server that serves the metadata. */
  private static TestWebServer server;
  
  static {    
    try {
      trustStore = KeyStoreUtils.loadKeyStore("src/test/resources/trust.jks", "secret", null);
      server = new TestWebServer(resourceProvider, "src/test/resources/localhost.jks", "secret");
    }
    catch (Exception e) {
    } 
  }

  /**
   * Starts the "remote" metadata service.
   * 
   * @throws Exception
   *           for errors
   */
  @BeforeClass
  static public void startServer() throws Exception {
    server.start();
  }

  /**
   * Stops the "remote" metadata service.
   * 
   * @throws Exception
   *           for errors
   */
  @AfterClass
  static public void stopServer() throws Exception {
    server.stop();
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractMetadataProvider createMetadataProvider(Resource resource) throws Exception {
    
    resourceProvider.setResource(resource);
    return new HTTPMetadataProvider(server.getUrl(), null, HTTPMetadataProvider.createDefaultHttpClient(trustStore, null)); 
  }

  /**
   * Simple class holding the metadata (accessed by the web server). 
   */
  private static class MetadataResourceProvider implements TestWebServer.ResourceProvider {

    private Resource resource;

    public void setResource(Resource resource) {
      this.resource = resource;
    }

    @Override
    public Resource getResource() {
      return this.resource;
    }
  }

}
