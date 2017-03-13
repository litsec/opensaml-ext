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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.litsec.opensaml.config.OpenSAMLInitializer;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Test cases for {@code OpenSAMLInitializerBean}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/opensaml-init.xml"})
public class OpenSAMLInitializerBeanTest {

  @Autowired
  private ApplicationContext applicationContext;
  
  @Test
  public void testOpenSAMLInitializer() throws Exception {
    
    Assert.assertNotNull(this.applicationContext.getBean(OpenSAMLInitializerBean.class));
    Assert.assertTrue(OpenSAMLInitializer.getInstance().isInitialized());
    
    // OpenSAML is initialized, so this should work ...    
    EntityDescriptor ed = ObjectUtils.createSamlObject(EntityDescriptor.class); 
    Assert.assertNotNull(ed);
  }

}
