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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.ext.saml2mdui.UIInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.TestHelper;
import se.litsec.opensaml.core.LocalizedString;
import se.litsec.opensaml.saml2.metadata.build.UIInfoBuilderTest;

/**
 * Test cases for {@code UIInfoFactoryBean}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/factory-test.xml" })
public class UIInfoFactoryBeanTest extends OpenSAMLTestBase {
  
  public static final List<Logo> LOGOS_LIST = Arrays.asList(UIInfoBuilderTest.LOGOS);
  public static final List<LocalizedString> INFORMATION_URLS_LIST = Arrays.asList(UIInfoBuilderTest.INFORMATION_URLS);
  public static final List<LocalizedString> PRIVACY_STATEMENT_URLS_LIST = Arrays.asList(UIInfoBuilderTest.PRIVACY_STATEMENT_URLS);

  @Autowired private UIInfo uiInfoBean;

  @Autowired private Properties testProperties;

  /**
   * Tests that the UIInfo bean has been correctly set up.
   * 
   * @throws Exception
   */
  @Test
  public void testFactoryFromSpringContext() throws Exception {

    Assert.assertNotNull(this.uiInfoBean);
    
    UIInfoBuilderTest.assertUIInfo(this.uiInfoBean, 
      TestHelper.getLocalizedStringArray(this.testProperties, "metadata.uiinfo.display-names"),
      UIInfoBuilderTest.KEYWORDS, null,
      TestHelper.getLocalizedStringArray(this.testProperties, "metadata.uiinfo.descriptions"),
      UIInfoBuilderTest.LOGOS,
      UIInfoBuilderTest.INFORMATION_URLS,
      UIInfoBuilderTest.PRIVACY_STATEMENT_URLS);
  }

  /**
   * Tests creating a full UIINfo with all elements set.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testCreateFull() throws Exception {

    UIInfoFactoryBean factory = new UIInfoFactoryBean();

    factory.setDisplayNames(Arrays.asList(UIInfoBuilderTest.DISPLAY_NAMES));
    factory.setKeywords(UIInfoBuilderTest.KEYWORDS);
    factory.setDescriptions(Arrays.asList(UIInfoBuilderTest.DESCRIPTIONS));
    factory.setLogos(Arrays.asList(UIInfoBuilderTest.LOGOS));
    factory.setInformationURLs(Arrays.asList(UIInfoBuilderTest.INFORMATION_URLS));
    factory.setPrivacyStatementURLs(Arrays.asList(UIInfoBuilderTest.PRIVACY_STATEMENT_URLS));

    factory.afterPropertiesSet();

    UIInfo uiInfo = factory.getObject();

    UIInfoBuilderTest.assertUIInfo(uiInfo, UIInfoBuilderTest.DISPLAY_NAMES, UIInfoBuilderTest.KEYWORDS, null,
      UIInfoBuilderTest.DESCRIPTIONS, UIInfoBuilderTest.LOGOS, UIInfoBuilderTest.INFORMATION_URLS,
      UIInfoBuilderTest.PRIVACY_STATEMENT_URLS);

    // Test using singleton = false
    //
    factory = new UIInfoFactoryBean();
    factory.setSingleton(false);

    factory.setDisplayNames(Arrays.asList(UIInfoBuilderTest.DISPLAY_NAMES));
    factory.setKeywords(UIInfoBuilderTest.KEYWORDS);
    factory.setDescriptions(Arrays.asList(UIInfoBuilderTest.DESCRIPTIONS));
    factory.setLogos(Arrays.asList(UIInfoBuilderTest.LOGOS));

    factory.afterPropertiesSet();
    uiInfo = factory.getObject();

    Assert.assertNotNull(uiInfo);
    UIInfoBuilderTest.assertUIInfo(uiInfo, UIInfoBuilderTest.DISPLAY_NAMES, UIInfoBuilderTest.KEYWORDS, null,
      UIInfoBuilderTest.DESCRIPTIONS, UIInfoBuilderTest.LOGOS, null, null);
  }

  /**
   * Test where not all elements and attributes are set.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testSimple() throws Exception {

    UIInfoFactoryBean factory = new UIInfoFactoryBean();

    LocalizedString[] displayNames = new LocalizedString[] {
        new LocalizedString("Litsec legitimering", "sv"),
        new LocalizedString("Litsec authentication", Locale.ENGLISH) };

    LocalizedString description = new LocalizedString("Litsecs tjänst för legitimering", "sv");

    Logo[] logos = new Logo[] {
        UIInfoBuilderTest.createLogo("http://www.litsec.se/logo-sv.jpg", null, 16, 16),
        UIInfoBuilderTest.createLogo("http://www.litsec.se/logo-sv-large.jpg", null, 200, 200) };

    LocalizedString informationURL = new LocalizedString("http://www.litsec.se", "sv");

    factory.setDisplayNames(Arrays.asList(displayNames));
    factory.setDescriptions(Arrays.asList(description));
    factory.setLogos(Arrays.asList(logos));
    factory.setInformationURLs(Arrays.asList(informationURL));

    factory.afterPropertiesSet();

    UIInfo uiInfo = factory.getObject();

    UIInfoBuilderTest.assertUIInfo(uiInfo, displayNames, null, null, new LocalizedString[] { description }, logos,
      new LocalizedString[] { informationURL }, null);
  }

}
