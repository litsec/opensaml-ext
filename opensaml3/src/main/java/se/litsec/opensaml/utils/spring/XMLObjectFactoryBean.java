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
package se.litsec.opensaml.utils.spring;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

/**
 * A Spring factory bean that creates OpenSAML {@link XMLObject} instances.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class XMLObjectFactoryBean extends AbstractFactoryBean<XMLObject> {
  
  /** The resource to read from. */
  private Resource resource;
  
  /**
   * Constructor assigning the resource to unmarshall the XMLObject from.
   * @param resource the resource
   */
  public XMLObjectFactoryBean(Resource resource) {
    this.resource = resource;
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return XMLObject.class;
  }

  /** {@inheritDoc} */
  @Override
  protected XMLObject createInstance() throws Exception {
    return XMLObjectSupport.unmarshallFromInputStream(XMLObjectProviderRegistrySupport.getParserPool(), this.resource.getInputStream());
  }

}
