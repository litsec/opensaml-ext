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
package se.litsec.opensaml.saml2.metadata.build.spring;

import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.core.io.Resource;

import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Factory bean for creating an {@link org.opensaml.saml2.metadata.EntitiesDescriptor} object from a resource.
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
