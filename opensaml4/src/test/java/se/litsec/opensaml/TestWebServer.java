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
package se.litsec.opensaml;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.springframework.core.io.Resource;

/**
 * Class for supporting test cases that need a web server.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class TestWebServer {

  /** The web server. */
  private Server server;

  /** The URL that is exposed by the web server. */
  private String url;

  /**
   * Constructor setting up the web server.
   * 
   * @param resourceProvider
   *          the provider handling the data
   * @param keyStorePath
   *          the path to the keystore holding the private key for the web server (may be {@code null})
   * @param keyStorePassword
   *          the password for the keystore
   */
  public TestWebServer(ResourceProvider resourceProvider, String keyStorePath, String keyStorePassword) {
    QueuedThreadPool serverThreads = new QueuedThreadPool();
    serverThreads.setName("server");
    this.server = new Server(serverThreads);

    SslContextFactory.Server contextFactory = null;
    if (keyStorePath != null) {
      contextFactory = new SslContextFactory.Server();
      contextFactory.setTrustAll(true);
      contextFactory.setKeyStorePath(keyStorePath);
      contextFactory.setKeyStorePassword(keyStorePassword);
      contextFactory.setMaxCertPathLength(-1);
      contextFactory.setProtocol("TLS");
    }

    ServerConnector connector = new ServerConnector(this.server, contextFactory);
    connector.setHost("localhost");
    this.server.addConnector(connector);
    server.setHandler(new ResourceHandler(resourceProvider));
  }

  /**
   * Starts the metadata service.
   * 
   * @throws Exception
   *           if the server fails to start
   */
  public void start() throws Exception {
    this.server.start();
    this.url = this.server.getURI().toURL().toString();
  }

  /**
   * Stops the metadata service.
   * 
   * @throws Exception
   *           if the service fails to stop
   */
  public void stop() throws Exception {
    if (this.server != null && this.server.isStarted() && !this.server.isStopped()) {
      this.server.stop();
    }
  }

  /**
   * Returns the URL for the server
   * 
   * @return the URL
   */
  public String getUrl() {
    return this.url;
  }

  /**
   * Simple interface for a resource provider.
   */
  @FunctionalInterface
  public interface ResourceProvider {
    Resource getResource();
  }

  /**
   * The {@code ResourceHandler} that is used by the server.
   */
  public static class ResourceHandler extends AbstractHandler {

    private ResourceProvider resourceProvider;

    public ResourceHandler(ResourceProvider resourceProvider) {
      this.resourceProvider = resourceProvider;
    }

    @Override
    public void handle(String target, Request baseRequest, jakarta.servlet.http.HttpServletRequest request,
        jakarta.servlet.http.HttpServletResponse response) throws IOException, jakarta.servlet.ServletException {

      response.getOutputStream().write(IOUtils.toByteArray(this.resourceProvider.getResource().getInputStream()));
      baseRequest.setHandled(true);

    }

  }

}
