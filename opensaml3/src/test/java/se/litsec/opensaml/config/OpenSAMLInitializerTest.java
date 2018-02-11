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
package se.litsec.opensaml.config;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.common.SAMLObjectBuilder;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;

/**
 * Test cases for {@link OpenSAMLInitializer}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
@Ignore
public class OpenSAMLInitializerTest {

  @Test
  public void testOpenSAMLInitializer() throws Exception {
    
    // If the OpenSAML library isn't initialized, this shouldn't work ...
    try {
      this.createEntityDescriptor();
      Assert.fail("Expected exception since OpenSAML hasn't been initialized");
    }
    catch (Exception e) {      
    }
    
    // Now, initialize OpenSAML and try again ...
    
    OpenSAMLInitializer initializer = OpenSAMLInitializer.getInstance();
    initializer.initialize();

    Assert.assertTrue(initializer.isInitialized());

    EntityDescriptor ed = this.createEntityDescriptor();
    Assert.assertNotNull(ed);
  }

  private EntityDescriptor createEntityDescriptor() throws Exception {
    SAMLObjectBuilder<EntityDescriptor> builder = (SAMLObjectBuilder<EntityDescriptor>) XMLObjectProviderRegistrySupport.getBuilderFactory()
      .<EntityDescriptor> getBuilderOrThrow(EntityDescriptor.DEFAULT_ELEMENT_NAME);
    return builder.buildObject();
  }

}
