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
