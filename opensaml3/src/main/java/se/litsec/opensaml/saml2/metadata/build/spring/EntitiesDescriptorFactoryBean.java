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
package se.litsec.opensaml.saml2.metadata.build.spring;

import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Factory bean for creating an {@link EntitiesDescriptor} object from a resource.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class EntitiesDescriptorFactoryBean extends AbstractFactoryBean<EntitiesDescriptor> {

  /** The resource to read from. */
  private Resource resource;

  /**
   * Constructor taking the resource that contains the object to read.
   * 
   * @param resource
   *          the resource
   */
  public EntitiesDescriptorFactoryBean(Resource resource) {
    this.resource = resource;
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return EntitiesDescriptor.class;
  }

  /** {@inheritDoc} */
  @Override
  protected final EntitiesDescriptor createInstance() throws Exception {
    return ObjectUtils.unmarshall(this.resource.getInputStream(), EntitiesDescriptor.class);
  }
}
