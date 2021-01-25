/*
 * Copyright 2016-2018 Litsec AB
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
