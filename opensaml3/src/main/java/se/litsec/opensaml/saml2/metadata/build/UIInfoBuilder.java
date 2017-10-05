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
import java.util.Map;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.ext.saml2mdui.Description;
import org.opensaml.saml.ext.saml2mdui.DisplayName;
import org.opensaml.saml.ext.saml2mdui.InformationURL;
import org.opensaml.saml.ext.saml2mdui.Keywords;
import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.ext.saml2mdui.PrivacyStatementURL;
import org.opensaml.saml.ext.saml2mdui.UIInfo;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.LocalizedString;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * A builder for creating {@link UIInfo} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class UIInfoBuilder extends AbstractSAMLObjectBuilder<UIInfo> {

  /**
   * Creates a builder instance.
   * 
   * @return a builder instance
   */
  public static UIInfoBuilder builder() {
    return new UIInfoBuilder();
  }

  /** {@inheritDoc} */
  @Override
  protected Class<UIInfo> getObjectType() {
    return UIInfo.class;
  }

  /**
   * Assigns the display names.
   * 
   * @param displayNames
   *          the names
   * @return the builder
   */
  public UIInfoBuilder displayNames(List<LocalizedString> displayNames) {
    if (displayNames != null) {
      for (LocalizedString s : displayNames) {
        DisplayName dn = ObjectUtils.createSamlObject(DisplayName.class);
        dn.setValue(s.getLocalString());
        dn.setXMLLang(s.getLanguage());
        this.object().getDisplayNames().add(dn);
      }
    }
    return this;
  }

  /**
   * @see #displayNames(List)
   * 
   * @param displayNames
   *          the names
   * @return the builder
   */
  public UIInfoBuilder displayNames(LocalizedString... displayNames) {
    return this.displayNames(displayNames != null ? Arrays.asList(displayNames) : null);
  }

  /**
   * Assigns the keywords.
   * 
   * @param keywords
   *          the keywords where the map keys are language tags
   * @return the builder
   */
  public UIInfoBuilder keywords(Map<String, List<String>> keywords) {
    if (keywords != null) {
      for (Map.Entry<String, List<String>> e : keywords.entrySet()) {
        Keywords kw = ObjectUtils.createSamlObject(Keywords.class);
        if (!e.getKey().isEmpty()) {
          kw.setXMLLang(e.getKey());
        }
        kw.setKeywords(e.getValue());
        this.object().getKeywords().add(kw);
      }
    }
    return this;
  }

  /**
   * Assigns a set of keywords that do not have the language tag.
   * 
   * @param keywords
   *          the keywords
   * @return the builder
   */
  public UIInfoBuilder keywords(List<String> keywords) {
    if (keywords != null) {
      Map<String, List<String>> m = new HashMap<>();
      m.put("", keywords);
      return this.keywords(m);
    }
    else {
      return this;
    }
  }

  /**
   * @see #keywords(List)
   * 
   * @param keywords
   *          the keywords
   * @return the builder
   */
  public UIInfoBuilder keywords(String... keywords) {
    return this.keywords(keywords != null ? Arrays.asList(keywords) : null);
  }

  /**
   * Assigns the descriptions.
   * 
   * @param descriptions
   *          the descriptions
   * @return the builder
   */
  public UIInfoBuilder descriptions(List<LocalizedString> descriptions) {
    if (descriptions != null) {
      for (LocalizedString s : descriptions) {
        Description d = ObjectUtils.createSamlObject(Description.class);
        d.setValue(s.getLocalString());
        d.setXMLLang(s.getLanguage());
        this.object().getDescriptions().add(d);
      }
    }
    return this;
  }

  /**
   * @see #descriptions(List)
   * 
   * @param descriptions
   *          the descriptions
   * @return the builder
   */
  public UIInfoBuilder descriptions(LocalizedString... descriptions) {
    return this.descriptions(descriptions != null ? Arrays.asList(descriptions) : null);
  }

  /**
   * Assigns the logotypes.
   * 
   * @param logos
   *          the logos (will be cloned before assignment)
   * @return the builder
   */
  public UIInfoBuilder logos(List<Logo> logos) {
    try {
      if (logos != null) {
        for (Logo logo : logos) {
          this.object().getLogos().add(XMLObjectSupport.cloneXMLObject(logo));
        }
      }
      return this;
    }
    catch (UnmarshallingException | MarshallingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * @see #logos(List)
   * 
   * @param logos
   *          the logos (will be cloned before assignment)
   * @return the builder
   */
  public UIInfoBuilder logos(Logo... logos) {
    return this.logos(logos != null ? Arrays.asList(logos) : null);
  }

  /**
   * Assigns the information URL:s.
   * 
   * @param informationURLs
   *          the information URL:s
   * @return the builder
   */
  public UIInfoBuilder informationURLs(List<LocalizedString> informationURLs) {
    if (informationURLs != null) {
      for (LocalizedString u : informationURLs) {
        InformationURL url = ObjectUtils.createSamlObject(InformationURL.class);
        url.setValue(u.getLocalString());
        url.setXMLLang(u.getLanguage());
        this.object().getInformationURLs().add(url);
      }
    }
    return this;
  }

  /**
   * @see #informationURLs(List)
   * 
   * @param informationURLs
   *          the information URL:s
   * @return the builder
   */
  public UIInfoBuilder informationURLs(LocalizedString... informationURLs) {
    return this.informationURLs(informationURLs != null ? Arrays.asList(informationURLs) : null);
  }

  /**
   * Assigns the privacy statement URL:s.
   * 
   * @param privacyStatementURLs
   *          the URL:s
   * @return the builder
   */
  public UIInfoBuilder privacyStatementURLs(List<LocalizedString> privacyStatementURLs) {
    if (privacyStatementURLs != null) {
      for (LocalizedString u : privacyStatementURLs) {
        PrivacyStatementURL url = ObjectUtils.createSamlObject(PrivacyStatementURL.class);
        url.setValue(u.getLocalString());
        url.setXMLLang(u.getLanguage());
        this.object().getPrivacyStatementURLs().add(url);
      }
    }
    return this;
  }

  /**
   * @see #privacyStatementURLs(List)
   * 
   * @param privacyStatementURLs
   *          the URL:s
   * @return the builder
   */
  public UIInfoBuilder privacyStatementURLs(LocalizedString... privacyStatementURLs) {
    return this.privacyStatementURLs(privacyStatementURLs != null ? Arrays.asList(privacyStatementURLs) : null);
  }

}
