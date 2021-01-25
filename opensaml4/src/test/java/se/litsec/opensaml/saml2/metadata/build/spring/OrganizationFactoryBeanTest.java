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
package se.litsec.opensaml.saml2.metadata.build.spring;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.metadata.Organization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.litsec.opensaml.TestHelper;
import se.litsec.opensaml.core.LocalizedString;

/**
 * Test cases for the {@code OrganizationFactoryBean}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/factory-test.xml"})
public class OrganizationFactoryBeanTest {
  
  @Autowired
  private Organization organization;
  
  @Autowired
  private Properties testProperties;
  
  /**
   * Tests that the organization bean has been correctly set up.
   * 
   * @throws Exception
   */
  @Test
  public void testFactoryFromSpringContext() throws Exception {
    
    Assert.assertNotNull(this.organization);

    LocalizedString[] names = TestHelper.getLocalizedStringArray(this.testProperties, "metadata.organization.names");        
    Assert.assertEquals(names.length, this.organization.getOrganizationNames().size());    
    for (int i = 0; i < names.length; i++) {
      Assert.assertEquals(names[i].getLocalString(), this.organization.getOrganizationNames().get(i).getValue());
      Assert.assertEquals(names[i].getLanguage(), this.organization.getOrganizationNames().get(i).getXMLLang());
    }    

    LocalizedString[] displayNames = TestHelper.getLocalizedStringArray(this.testProperties, "metadata.organization.display-names");
    Assert.assertEquals(displayNames.length, this.organization.getDisplayNames().size());    
    for (int i = 0; i < displayNames.length; i++) {
      Assert.assertEquals(displayNames[i].getLocalString(), this.organization.getDisplayNames().get(i).getValue());
      Assert.assertEquals(displayNames[i].getLanguage(), this.organization.getDisplayNames().get(i).getXMLLang());
    }
    
    LocalizedString url = TestHelper.getLocalizedString(this.testProperties, "metadata.organization.url");
    Assert.assertEquals(1, this.organization.getURLs().size());
    Assert.assertEquals(url.getLocalString(), this.organization.getURLs().get(0).getURI());
    Assert.assertEquals(url.getLanguage(), this.organization.getURLs().get(0).getXMLLang());
  }

}
