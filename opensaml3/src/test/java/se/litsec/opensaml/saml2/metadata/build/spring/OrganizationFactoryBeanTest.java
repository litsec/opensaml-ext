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
    Assert.assertEquals(url.getLocalString(), this.organization.getURLs().get(0).getValue());
    Assert.assertEquals(url.getLanguage(), this.organization.getURLs().get(0).getXMLLang());
  }

}
