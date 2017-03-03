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
package se.litsec.opensaml;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    SslContextFactory contextFactory = null;
    if (keyStorePath != null) {
      contextFactory = new SslContextFactory(true);
      contextFactory.setKeyStorePath(keyStorePath);
      contextFactory.setKeyStorePassword(keyStorePassword);
      contextFactory.setMaxCertPathLength(-1);
      contextFactory.setProtocol("TLS");
      //contextFactory.setIncludeCipherSuites("TLS_RSA_WITH_AES_128_CBC_SHA256");
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
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException,
        ServletException {

      response.getOutputStream().write(IOUtils.toByteArray(this.resourceProvider.getResource().getInputStream()));
      baseRequest.setHandled(true);
    }

  }

}
