/*
 * Copyright 2016-2021 Litsec AB
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
package se.litsec.opensaml.saml2.metadata.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Base class for running tests for metadata providers.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
@RunWith(JUnitParamsRunner.class)
public abstract class BaseMetadataProviderTest extends OpenSAMLTestBase {

  public static final String TEST_IDP = "https://idp.svelegtest.se/idp";
  public static final String TEST_SP = "https://eid.svelegtest.se/validation/testsp1";

  /**
   * Must be implemented by subclasses that creates a provider instance and assigns the metadata identified by the
   * supplied resource.
   * 
   * @param resource
   *          metadata source
   * @return a provider instance
   * @throws Exception
   *           for errors
   */
  protected abstract AbstractMetadataProvider createMetadataProvider(Resource resource) throws Exception;

  /**
   * Tests the methods that gets entity descriptors from a provider.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testGetMethods() throws Exception {

    MetadataProvider provider = this.createMetadataProvider(new ClassPathResource("/metadata/sveleg-fedtest.xml"));

    try {
      provider.initialize();

      EntityDescriptor ed = provider.getEntityDescriptor(TEST_IDP);
      Assert.assertNotNull(String.format("EntityDescriptor for '%s' was not found", TEST_IDP), ed);

      ed = provider.getEntityDescriptor(TEST_SP);
      Assert.assertNotNull(String.format("EntityDescriptor for '%s' was not found", TEST_SP), ed);

      IDPSSODescriptor idpDescriptor = provider.getIDPSSODescriptor(TEST_IDP);
      Assert.assertNotNull(String.format("IDPSSODescriptor for '%s' was not found", TEST_IDP), idpDescriptor);

      SPSSODescriptor spDescriptor = provider.getSPSSODescriptor(TEST_SP);
      Assert.assertNotNull(String.format("SPSSODescriptor for '%s' was not found", TEST_SP), spDescriptor);

      List<EntityDescriptor> idps = provider.getIdentityProviders();
      Assert.assertEquals("Expected 2 IdPs", 2, idps.size());

      List<EntityDescriptor> sps = provider.getServiceProviders();
      Assert.assertEquals("Expected 43 SPs", 43, sps.size());

      XMLObject xmlObject = provider.getMetadata();
      Assert.assertNotNull("Could not get metadata XMLObject from provider", xmlObject);
      Assert.assertTrue("Expected EntitiesDescriptor", xmlObject instanceof EntitiesDescriptor);

      Element xml = provider.getMetadataDOM();
      Assert.assertNotNull("Could not get metadata DOM from provider", xml);
    }
    finally {
      if (provider.isInitialized()) {
        provider.destroy();
      }
    }
  }
  
  /**
   * Tests the iterator methods for the provider.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testIterators() throws Exception {

    // Try parsing a file with mixed EntityDescriptors and EntitiesDescriptors.
    MetadataProvider provider = this.createMetadataProvider(new ClassPathResource("/metadata/sveleg-fedtest-complex.xml"));

    try {
      provider.initialize();
      
      List<EntityDescriptor> list = new ArrayList<EntityDescriptor>();
      Iterable<EntityDescriptor> i = provider.iterator();
      i.forEach(list::add);
      Assert.assertEquals("Expected 45 descriptors", 45, list.size());

      list.clear();
      i = provider.iterator(IDPSSODescriptor.DEFAULT_ELEMENT_NAME);
      i.forEach(list::add);
      Assert.assertEquals("Expected 2 descriptors", 2, list.size());

      list.clear();
      i = provider.iterator(SPSSODescriptor.DEFAULT_ELEMENT_NAME);
      i.forEach(list::add);
      Assert.assertEquals("Expected 43 descriptors", 43, list.size());
    }
    finally {
      if (provider.isInitialized()) {
        provider.destroy();
      }
    }
  }
  
  /**
   * Tests getting the DOM of the entire metadata held by the provider.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testDOM() throws Exception {
    MetadataProvider provider = this.createMetadataProvider(new ClassPathResource("/metadata/sveleg-fedtest.xml"));    
    try {
      provider.initialize();
      Element dom = provider.getMetadataDOM();
      EntitiesDescriptor ed = ObjectUtils.unmarshall(dom, EntitiesDescriptor.class);
      for (EntityDescriptor e : ed.getEntityDescriptors()) {
        EntityDescriptor e2 = provider.getEntityDescriptor(e.getEntityID());
        Assert.assertNotNull(String.format("EntityDescriptor for '%s' was not found", e.getEntityID()), e2);
      }
    }
    finally {
      if (provider.isInitialized()) {
        provider.destroy();
      }
    }
  }

  /**
   * Tests schema validation filter.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testSchemaValidation() throws Exception {
    AbstractMetadataProvider provider = this.createMetadataProvider(new ClassPathResource("/metadata/sveleg-fedtest.xml"));
    
    try {
      provider.setFailFastInitialization(true);
      provider.setPerformSchemaValidation(true);
      provider.initialize();
      Element dom = provider.getMetadataDOM();
      Assert.assertNotNull(dom);
    }
    finally {
      if (provider.isInitialized()) {
        provider.destroy();
      }
    }

    provider = this.createMetadataProvider(new ClassPathResource("/metadata/sveleg-fedtest-badschema.xml"));     
    try {
      provider.setFailFastInitialization(true);
      provider.setPerformSchemaValidation(true);
      provider.initialize();
      Assert.fail("Expected schema validation error");
    }
    catch (ComponentInitializationException e) {
    }
    finally {
      if (provider.isInitialized()) {
        provider.destroy();
      }
    }
  }
  
  /**
   * Tests filtering based on predicates.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  @Parameters(method = "parametersFortestPredicates")
  public void testPredicates(Predicate<EntityDescriptor> includePredicate, int expectedMatches) throws Exception {
    
    AbstractMetadataProvider provider = this.createMetadataProvider(new ClassPathResource("/metadata/sveleg-fedtest.xml"));    
    try {
      provider.setInclusionPredicates(Arrays.asList(includePredicate));
      provider.initialize();
      List<EntityDescriptor> list = new ArrayList<EntityDescriptor>();
      Iterable<EntityDescriptor> i = provider.iterator();
      i.forEach(list::add);
      Assert.assertEquals(String.format("Expected %d descriptors", expectedMatches), expectedMatches, list.size());
    }
    finally {
      if (provider.isInitialized()) {
        provider.destroy();
      }
    }
  }

  public Object[] parametersFortestPredicates() {
    return new Object[] { 
        new Object[] { MetadataProviderPredicates.includeOnlyIDPs(), 2 }, 
        new Object[] { MetadataProviderPredicates.includeOnlyIDPsAndMe(TEST_SP), 3 }, 
        new Object[] { MetadataProviderPredicates.includeOnlySPs(), 43 } };
  }

  /**
   * Tests for cases when we ask for non existing descriptors.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testNotFound() throws Exception {

    MetadataProvider provider = this.createMetadataProvider(new ClassPathResource("/metadata/sveleg-fedtest.xml"));    

    try {
      provider.initialize();
      
      EntityDescriptor ed = provider.getEntityDescriptor("http://not.an.entity");
      Assert.assertNull("EntityDescriptor for 'http://not.an.entity' was found!!?", ed);

      IDPSSODescriptor idpDescriptor = provider.getIDPSSODescriptor(TEST_SP);
      Assert.assertNull(String.format("An IDPSSODescriptor for '%s' was found, but it is an SP!", TEST_SP), idpDescriptor);

      SPSSODescriptor spDescriptor = provider.getSPSSODescriptor(TEST_IDP);
      Assert.assertNull(String.format("An SPSSODescriptor for '%s' was found, but it is an IdP!", TEST_SP), spDescriptor);
    }
    finally {
      if (provider.isInitialized()) {
        provider.destroy();
      }
    }
  }


}
