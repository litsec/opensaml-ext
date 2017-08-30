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

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import se.litsec.opensaml.OpenSAMLTestBase;

/**
 * Test cases for {@link XMLObjectFactoryBean}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class XMLObjectFactoryBeanTest extends OpenSAMLTestBase {

  public static final Resource METADATA_RESOURCE = new ClassPathResource("/metadata/sveleg-fedtest.xml");
  
  @Test
  public void testEntitiesDescriptor() throws Exception {
    XMLObjectFactoryBean factory = new XMLObjectFactoryBean(METADATA_RESOURCE);
    factory.afterPropertiesSet();
    
    XMLObject object = factory.getObject();
    Assert.assertNotNull(object);
    Assert.assertTrue(object instanceof EntitiesDescriptor);
  }
}
