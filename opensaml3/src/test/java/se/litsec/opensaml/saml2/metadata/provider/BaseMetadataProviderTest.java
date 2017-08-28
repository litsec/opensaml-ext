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
package se.litsec.opensaml.saml2.metadata.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

  private static final String TEST_IDP = "https://idp.svelegtest.se/idp";
  private static final String TEST_SP = "https://eid.svelegtest.se/validation/testsp1";

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

      Optional<EntityDescriptor> ed = provider.getEntityDescriptor(TEST_IDP);
      Assert.assertTrue(String.format("EntityDescriptor for '%s' was not found", TEST_IDP), ed.isPresent());

      ed = provider.getEntityDescriptor(TEST_SP);
      Assert.assertTrue(String.format("EntityDescriptor for '%s' was not found", TEST_SP), ed.isPresent());

      Optional<IDPSSODescriptor> idpDescriptor = provider.getIDPSSODescriptor(TEST_IDP);
      Assert.assertTrue(String.format("IDPSSODescriptor for '%s' was not found", TEST_IDP), idpDescriptor.isPresent());

      Optional<SPSSODescriptor> spDescriptor = provider.getSPSSODescriptor(TEST_SP);
      Assert.assertTrue(String.format("SPSSODescriptor for '%s' was not found", TEST_SP), spDescriptor.isPresent());

      List<EntityDescriptor> idps = provider.getIdentityProviders();
      Assert.assertEquals("Expected 2 IdPs", 2, idps.size());

      List<EntityDescriptor> sps = provider.getServiceProviders();
      Assert.assertEquals("Expected 43 SPs", 43, sps.size());

      Optional<XMLObject> xmlObject = provider.getMetadata();
      Assert.assertTrue("Could not get metadata XMLObject from provider", xmlObject.isPresent());
      Assert.assertTrue("Expected EntitiesDescriptor", xmlObject.get() instanceof EntitiesDescriptor);

      Optional<Element> xml = provider.getMetadataDOM();
      Assert.assertTrue("Could not get metadata DOM from provider", xml.isPresent());
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
      Optional<Element> dom = provider.getMetadataDOM();
      EntitiesDescriptor ed = ObjectUtils.unmarshall(dom.get(), EntitiesDescriptor.class);
      for (EntityDescriptor e : ed.getEntityDescriptors()) {
        Optional<EntityDescriptor> e2 = provider.getEntityDescriptor(e.getEntityID());
        Assert.assertTrue(String.format("EntityDescriptor for '%s' was not found", e.getEntityID()), e2.isPresent());
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
      Optional<Element> dom = provider.getMetadataDOM();
      Assert.assertTrue(dom.isPresent());
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
      
      Optional<EntityDescriptor> ed = provider.getEntityDescriptor("http://not.an.entity");
      Assert.assertFalse("EntityDescriptor for 'http://not.an.entity' was found!!?", ed.isPresent());

      Optional<IDPSSODescriptor> idpDescriptor = provider.getIDPSSODescriptor(TEST_SP);
      Assert.assertFalse(String.format("An IDPSSODescriptor for '%s' was found, but it is an SP!", TEST_SP), idpDescriptor.isPresent());

      Optional<SPSSODescriptor> spDescriptor = provider.getSPSSODescriptor(TEST_IDP);
      Assert.assertFalse(String.format("An SPSSODescriptor for '%s' was found, but it is an IdP!", TEST_SP), spDescriptor.isPresent());
    }
    finally {
      if (provider.isInitialized()) {
        provider.destroy();
      }
    }
  }


}
