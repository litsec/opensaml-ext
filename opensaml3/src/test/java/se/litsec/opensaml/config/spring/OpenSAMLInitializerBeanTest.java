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
