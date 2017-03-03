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
    return new HTTPMetadataProvider(server.getUrl(), null, trustStore);
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
