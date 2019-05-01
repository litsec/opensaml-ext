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
package se.litsec.opensaml.config.spring;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import net.shibboleth.utilities.java.support.xml.ParserPool;
import se.swedenconnect.opensaml.OpenSAMLInitializer;
import se.swedenconnect.opensaml.OpenSAMLInitializerConfig;

/**
 * Bean for initializing the OpenSAML 3.X library.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see OpenSAMLInitializer
 */
public class OpenSAMLInitializerBean implements InitializingBean {

  /** The initializer bean may be assigned a configured parser pool. */
  private ParserPool parserPool;

  /** Additional custom configuration. */
  private OpenSAMLInitializerConfig[] customConfiguration;

  /**
   * Initializes the OpenSAML library.
   * 
   * @throws Exception
   *           for init errors
   */
  public void initialize() throws Exception {
    OpenSAMLInitializer initializer = OpenSAMLInitializer.getInstance();

    if (!initializer.isInitialized()) {
      if (this.parserPool != null) {
        initializer.setParserPool(this.parserPool);
      }
      initializer.initialize(this.customConfiguration);
    }
    else if (this.parserPool != null) {
      initializer.setParserPool(this.parserPool);
    }
  }

  /**
   * Set the global ParserPool to configure.
   * 
   * @param parserPool
   *          the parserPool to assign
   */
  public void setParserPool(ParserPool parserPool) {
    this.parserPool = parserPool;
  }

  /**
   * Sets additional configuration that should be provided in the
   * {@link OpenSAMLInitializer#initialize(OpenSAMLInitializerConfig...)} call.
   * 
   * @param customConfiguration
   *          additional configuration
   */
  public void setCustomConfiguration(List<OpenSAMLInitializerConfig> customConfiguration) {
    this.customConfiguration = customConfiguration != null
        ? customConfiguration.stream().toArray(OpenSAMLInitializerConfig[]::new)
        : new OpenSAMLInitializerConfig[] {};
  }

  /**
   * Invokes {@link #initialize()} after all properties have been assigned.
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    this.initialize();
  }

}
