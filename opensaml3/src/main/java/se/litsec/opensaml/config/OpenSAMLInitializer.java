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
package se.litsec.opensaml.config;

import java.util.HashMap;
import java.util.Map;

import org.opensaml.core.config.ConfigurationService;
import org.opensaml.core.config.InitializationService;
import org.opensaml.core.xml.config.XMLObjectProviderRegistry;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.BasicParserPool;
import net.shibboleth.utilities.java.support.xml.ParserPool;

/**
 * Singleton class for initialization and configuration of the OpenSAML library.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class OpenSAMLInitializer {

  /** Logger instance. */
  private Logger logger = LoggerFactory.getLogger(OpenSAMLInitializer.class);

  /** Whether this component has been initialized. */
  private boolean initialized;

  /** The initializer may be assigned a configured parser pool. */
  private ParserPool parserPool;

  /** Builder features for the default parser pool. */
  private static final Map<String, Boolean> builderFeatures;

  static {
    builderFeatures = new HashMap<>();
    builderFeatures.put("http://apache.org/xml/features/disallow-doctype-decl", Boolean.TRUE);
    builderFeatures.put("http://apache.org/xml/features/validation/schema/normalized-value", Boolean.FALSE);
    builderFeatures.put("http://javax.xml.XMLConstants/feature/secure-processing", Boolean.TRUE);
  }

  /** The singleton instance. */
  private static OpenSAMLInitializer INSTANCE = new OpenSAMLInitializer();

  /**
   * Returns the initializer instance.
   * 
   * @return the initializer instance
   */
  public static OpenSAMLInitializer getInstance() {
    return INSTANCE;
  }

  /**
   * Predicate that tells if the OpenSAML library already has been initialized.
   * 
   * @return if the library has been initialized {@code true} is returned, otherwise {@code false}
   */
  public boolean isInitialized() {
    return this.initialized;
  }

  /**
   * Initializes the OpenSAML library.
   * 
   * @throws Exception
   *           thrown if there is a problem initializing the library
   */
  public final synchronized void initialize() throws Exception {

    if (this.initialized) {
      logger.info("OpenSAML 3.X library has already been initialized");
      return;
    }

    logger.debug("Initializing OpenSAML 3.X library...");

    InitializationService.initialize();

    XMLObjectProviderRegistry registry;
    synchronized (ConfigurationService.class) {
      registry = ConfigurationService.get(XMLObjectProviderRegistry.class);
      if (registry == null) {
        logger.debug("XMLObjectProviderRegistry did not exist in ConfigurationService, will be created");
        registry = new XMLObjectProviderRegistry();
        ConfigurationService.register(XMLObjectProviderRegistry.class, registry);
      }
    }
    if (this.parserPool != null) {
      logger.debug("Installing configured parser pool to XMLObjectProviderRegistry...");
      registry.setParserPool(this.parserPool);
    }
    else if (registry.getParserPool() == null) {
      logger.debug("Installing default parser pool to XMLObjectProviderRegistry...");
      registry.setParserPool(createDefaultParserPool());
    }

    logger.info("OpenSAML library 3.X successfully initialized");

    this.initialized = true;
  }

  /**
   * Set the global ParserPool to configure.
   * 
   * @param parserPool
   *          the parserPool to assign
   */
  public void setParserPool(ParserPool parserPool) {
    this.parserPool = parserPool;
    if (this.isInitialized()) {
      logger.info("OpenSAML 3.X library has already been initialized - setting supplied parser pool to registry");
      XMLObjectProviderRegistrySupport.setParserPool(parserPool);
    }
  }

  /**
   * Creates a basic parser pool with default settings.
   * 
   * @return the default parser pool
   * @throws ComponentInitializationException
   *           for init errors
   */
  public static ParserPool createDefaultParserPool() throws ComponentInitializationException {
    BasicParserPool basicParserPool = new BasicParserPool();
    basicParserPool.setMaxPoolSize(100);
    basicParserPool.setCoalescing(true);
    basicParserPool.setIgnoreComments(true);
    basicParserPool.setIgnoreElementContentWhitespace(true);
    basicParserPool.setNamespaceAware(true);
    basicParserPool.setBuilderFeatures(builderFeatures);
    basicParserPool.initialize();
    return basicParserPool;
  }

  // Hidden constructor
  private OpenSAMLInitializer() {
  }

}
