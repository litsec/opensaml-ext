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
package se.litsec.opensaml.config.spring;

import org.springframework.beans.factory.InitializingBean;

import net.shibboleth.utilities.java.support.xml.ParserPool;
import se.litsec.opensaml.config.OpenSAMLInitializer;

/**
 * Bean for initializing the OpenSAML 3.X library.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see OpenSAMLInitializer
 */
public class OpenSAMLInitializerBean implements InitializingBean {

  /** The initializer bean may be assigned a configured parser pool. */
  private ParserPool parserPool;

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
      initializer.initialize();
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
   * Invokes {@link #initialize()} after all properties have been assigned.
   */
  @Override
  public void afterPropertiesSet() throws Exception {
    this.initialize();
  }

}
