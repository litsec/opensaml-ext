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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.saml2.metadata.EntitiesDescriptor;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Test cases for the {@code CompositeMetadataProvider} class.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class CompositeMetadataProviderTest extends OpenSAMLTestBase {

  private static final String TEST_IDP = "https://idp.svelegtest.se/idp";
  private static final String TEST_SP = "https://eid.svelegtest.se/validation/testsp1";

  private static final Resource part1 = new ClassPathResource("/metadata/sveleg-fedtest-part1.xml");
  private static final Resource part2 = new ClassPathResource("/metadata/sveleg-fedtest-part2.xml");
  private static final Resource part3 = new ClassPathResource("/metadata/sveleg-fedtest-part3.xml");

  private static MetadataProvider entireMetadataProvider;

  /**
   * We use a simple StaticMetadataProvider to hold the entire metadata.
   * 
   * @throws Exception
   *           for errors
   */
  @BeforeClass
  public static void setup() throws Exception {
    Resource entireMetadata = new ClassPathResource("/metadata/sveleg-fedtest.xml");
    Element entireMetadataDOM = XMLObjectProviderRegistrySupport.getParserPool()
      .parse(entireMetadata.getInputStream())
      .getDocumentElement();
    entireMetadataProvider = new StaticMetadataProvider(entireMetadataDOM);
    entireMetadataProvider.initialize();
  }

  /**
   * Destroys the metadata provider used.
   * 
   * @throws Exception
   *           for errors
   */
  @AfterClass
  public static void tearDown() throws Exception {
    if (entireMetadataProvider != null && entireMetadataProvider.isInitialized()) {      
      entireMetadataProvider.destroy();
    }
  }

  /**
   * We split /metadata/sveleg-fedtest.xml into three parts and verify the the {@code CompositeMetadataProvider} can
   * access all metadata using three different underlying providers.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testCompositeBasic() throws Exception {

    // Setup the composite provider with three providers (for the three parts)
    //
    CompositeMetadataProvider provider = new CompositeMetadataProvider("MetadataService", Arrays.asList(new FilesystemMetadataProvider(part1.getFile()),
      new FilesystemMetadataProvider(part2.getFile()), new FilesystemMetadataProvider(part3.getFile())));
    
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
      
      // Make sure that no signature is there, and so on
      EntitiesDescriptor metadata = (EntitiesDescriptor) xmlObject.get();
      Assert.assertNull("Expected no signature", metadata.getSignature());
      Assert.assertNull("Expected no cacheDuration attribute", metadata.getCacheDuration());
      Assert.assertNull("Expected no validUntil", metadata.getValidUntil());
      Assert.assertEquals(provider.getID(), metadata.getName());
      Assert.assertNotNull("Expected ID to be assigned", metadata.getID());

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
   * Tests getting the DOM of the entire metadata held by the provider.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testDOM() throws Exception {
    CompositeMetadataProvider provider = new CompositeMetadataProvider("MetadataService", Arrays.asList(new FilesystemMetadataProvider(part1.getFile()),
      new FilesystemMetadataProvider(part2.getFile()), new FilesystemMetadataProvider(part3.getFile())));

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


}
