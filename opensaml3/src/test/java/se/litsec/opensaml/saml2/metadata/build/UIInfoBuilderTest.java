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
package se.litsec.opensaml.saml2.metadata.build;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.saml.ext.saml2mdui.Keywords;
import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.ext.saml2mdui.UIInfo;

import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.core.LocalizedString;
import se.litsec.opensaml.saml2.metadata.build.spring.LogoFactoryBeanTest;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Test cases for {@code UIInfoBuilder}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class UIInfoBuilderTest extends OpenSAMLTestBase {

  public static final LocalizedString[] DISPLAY_NAMES = new LocalizedString[] {
      new LocalizedString("Litsec legitimering", "sv"),
      new LocalizedString("Litsec authentication", Locale.ENGLISH) };

  public static Map<String, List<String>> KEYWORDS;

  public static final LocalizedString[] DESCRIPTIONS = new LocalizedString[] {
      new LocalizedString("Litsecs tjänst för legitimering", "sv"),
      new LocalizedString("Litsec service for authentication", "en") };

  public static final Logo[] LOGOS = new Logo[] {
      createLogo("http://www.litsec.se/logo-sv.jpg", "sv", 16, 16),
      createLogo("http://www.litsec.se/logo-sv-large.jpg", "sv", 200, 200),
      createLogo("http://www.litsec.se/logo-en.jpg", "en", 16, 16) };

  public static final LocalizedString[] INFORMATION_URLS = new LocalizedString[] {
      new LocalizedString("http://www.litsec.se", "sv"),
      new LocalizedString("http://www.litsec.se/index-en.html", Locale.ENGLISH) };

  public static final LocalizedString[] PRIVACY_STATEMENT_URLS = new LocalizedString[] {
      new LocalizedString("http://www.litsec.se/privacy.html", "sv"),
      new LocalizedString("http://www.litsec.se/privacy-en.html", Locale.ENGLISH) };

  static {
    KEYWORDS = new HashMap<String, List<String>>();
    KEYWORDS.put(Locale.ENGLISH.getLanguage(), Arrays.asList("authentication", "SAML", "two-factor"));
    KEYWORDS.put("sv", Arrays.asList("legitimering"));
  }

  /**
   * Tests creating a full UIINfo with all elements set.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testCreateFull() throws Exception {

    UIInfo uiInfo = (new UIInfoBuilder())
      .displayNames(DISPLAY_NAMES)
      .keywords(KEYWORDS)
      .descriptions(DESCRIPTIONS)
      .logos(LOGOS)
      .informationURLs(INFORMATION_URLS)
      .privacyStatementURLs(PRIVACY_STATEMENT_URLS)
      .build();

    assertUIInfo(uiInfo, DISPLAY_NAMES, KEYWORDS, null, DESCRIPTIONS, LOGOS, INFORMATION_URLS, PRIVACY_STATEMENT_URLS);
  }

  /**
   * Test where not all elements and attributes are set.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testSimple() throws Exception {

    LocalizedString[] displayNames = new LocalizedString[] {
        new LocalizedString("Litsec legitimering", "sv"),
        new LocalizedString("Litsec authentication", Locale.ENGLISH) };

    String[] keywords = new String[] { "authentication", "SAML", "two-factor" };

    LocalizedString description = new LocalizedString("Litsecs tjänst för legitimering", "sv");

    Logo[] logos = new Logo[] {
        createLogo("http://www.litsec.se/logo-sv.jpg", null, 16, 16),
        createLogo("http://www.litsec.se/logo-sv-large.jpg", null, 200, 200) };

    LocalizedString informationURL = new LocalizedString("http://www.litsec.se", "sv");

    UIInfo uiInfo = (new UIInfoBuilder())
      .displayNames(displayNames)
      .keywords(keywords)
      .descriptions(description)
      .logos(logos)
      .informationURLs(informationURL)
      .build();

    assertUIInfo(uiInfo, displayNames, null, keywords, new LocalizedString[] { description }, logos, new LocalizedString[] {
        informationURL }, null);
  }

  public static Logo createLogo(String url, String language, Integer height, Integer width) {
    Logo logo = ObjectUtils.createSamlObject(Logo.class);
    logo.setURL(url);
    logo.setXMLLang(language);
    logo.setHeight(height);
    logo.setWidth(width);
    return logo;
  }

  public static void assertUIInfo(UIInfo uiInfo, LocalizedString[] displayNames, Map<String, List<String>> keywords, String[] keywordsArray,
      LocalizedString[] descriptions, Logo[] logos, LocalizedString[] informationURLs, LocalizedString[] privayStatementURLs) {

    Assert.assertEquals(displayNames != null ? displayNames.length : 0, uiInfo.getDisplayNames().size());
    for (int i = 0; i < uiInfo.getDisplayNames().size(); i++) {
      Assert.assertEquals(displayNames[i].getLanguage(), uiInfo.getDisplayNames().get(i).getXMLLang());
      Assert.assertEquals(displayNames[i].getLocalString(), uiInfo.getDisplayNames().get(i).getValue());
    }

    if (keywords != null) {
      Assert.assertEquals(keywords.keySet().size(), uiInfo.getKeywords().size());
      for (Keywords kw : uiInfo.getKeywords()) {
        List<String> words = keywords.get(kw.getXMLLang());
        Assert.assertNotNull(words);
        Assert.assertEquals(words, kw.getKeywords());
      }
    }
    if (keywordsArray != null) {
      Assert.assertEquals(1, uiInfo.getKeywords().size());
      Assert.assertNull(uiInfo.getKeywords().get(0).getXMLLang());
      Assert.assertEquals(Arrays.asList(keywordsArray), uiInfo.getKeywords().get(0).getKeywords());
    }

    Assert.assertEquals(descriptions != null ? descriptions.length : 0, uiInfo.getDescriptions().size());
    for (int i = 0; i < uiInfo.getDescriptions().size(); i++) {
      Assert.assertEquals(descriptions[i].getLanguage(), uiInfo.getDescriptions().get(i).getXMLLang());
      Assert.assertEquals(descriptions[i].getLocalString(), uiInfo.getDescriptions().get(i).getValue());
    }

    Assert.assertEquals(logos != null ? logos.length : 0, uiInfo.getLogos().size());
    for (int i = 0; i < uiInfo.getLogos().size(); i++) {
      Assert.assertTrue(LogoFactoryBeanTest.equals(logos[i], uiInfo.getLogos().get(i)));
    }

    Assert.assertEquals(informationURLs != null ? informationURLs.length : 0, uiInfo.getInformationURLs().size());
    for (int i = 0; i < uiInfo.getInformationURLs().size(); i++) {
      Assert.assertEquals(informationURLs[i].getLanguage(), uiInfo.getInformationURLs().get(i).getXMLLang());
      Assert.assertEquals(informationURLs[i].getLocalString(), uiInfo.getInformationURLs().get(i).getValue());
    }

    Assert.assertEquals(privayStatementURLs != null ? privayStatementURLs.length : 0, uiInfo.getPrivacyStatementURLs().size());
    for (int i = 0; i < uiInfo.getPrivacyStatementURLs().size(); i++) {
      Assert.assertEquals(privayStatementURLs[i].getLanguage(), uiInfo.getPrivacyStatementURLs().get(i).getXMLLang());
      Assert.assertEquals(privayStatementURLs[i].getLocalString(), uiInfo.getPrivacyStatementURLs().get(i).getValue());
    }
  }

}
