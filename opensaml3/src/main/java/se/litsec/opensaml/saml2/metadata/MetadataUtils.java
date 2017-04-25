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
package se.litsec.opensaml.saml2.metadata;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.ext.saml2mdui.Description;
import org.opensaml.saml.ext.saml2mdui.DisplayName;
import org.opensaml.saml.ext.saml2mdui.UIInfo;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.SSODescriptor;

/**
 * Utility methods for accessing metadata elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class MetadataUtils {

  /**
   * Finds the first extension matching the supplied type.
   * 
   * @param extensions
   *          the {@link Extensions} to search
   * @param clazz
   *          the extension type
   * @return the matching extension
   */
  public static <T> Optional<T> getMetadataExtension(Extensions extensions, Class<T> clazz) {
    if (extensions == null) {
      return Optional.empty();
    }
    return extensions.getOrderedChildren()
      .stream()
      .filter(e -> clazz.isAssignableFrom(e.getClass()))
      .map(e -> clazz.cast(e))
      .findFirst();
  }

  /**
   * Finds all extensions matching the supplied type.
   * 
   * @param extensions
   *          the {@link Extensions} to search
   * @param clazz
   *          the extension type
   * @return a (possibly empty) list of extensions elements of the given type
   */
  public static <T> List<T> getMetadataExtensions(Extensions extensions, Class<T> clazz) {
    if (extensions == null) {
      return Collections.emptyList();
    }
    return extensions.getOrderedChildren()
      .stream()
      .filter(e -> clazz.isAssignableFrom(e.getClass()))
      .map(e -> clazz.cast(e))
      .collect(Collectors.toList());
  }

  /**
   * Returns the {@code EntityAttributes} element that is placed as an extension to the supplied entity descriptor.
   * 
   * @param ed
   *          the entity descriptor
   * @return the EntityAttributes element
   */
  public static Optional<EntityAttributes> getEntityAttributes(EntityDescriptor ed) {
    return getMetadataExtension(ed.getExtensions(), EntityAttributes.class);
  }

  /**
   * Utility method that returns a list of the {@code mdui:DisplayName} element found in the SSO descriptor extension of
   * the supplied entity descriptor.
   * 
   * @param ed
   *          the entity descriptor
   * @return a (possibly empty) list of {@code DisplayName} elements
   */
  public static List<DisplayName> getUiDisplayNames(EntityDescriptor ed) {
    SSODescriptor ssoDescriptor = MetadataUtils.getSSODescriptor(ed);
    if (ssoDescriptor == null) {
      return Collections.emptyList();
    }
    Optional<UIInfo> uiInfo = getMetadataExtension(ssoDescriptor.getExtensions(), UIInfo.class);
    return uiInfo.isPresent() ? uiInfo.get().getDisplayNames() : Collections.emptyList();
  }

  /**
   * Utility method that returns the {@code mdui:DisplayName} element for the given language tag from the SSO descriptor
   * extension of the supplied entity descriptor.
   * 
   * @param ed
   *          the entity descriptor
   * @param language
   *          the language tag
   * @return the display name for the given language
   */
  public static Optional<String> getUiDisplayName(EntityDescriptor ed, String language) {
    return getUiDisplayNames(ed).stream()
      .filter(dn -> language.equals(dn.getXMLLang()))
      .map(dn -> dn.getValue())
      .findFirst();
  }

  /**
   * Utility method that returns a list of the {@code mdui:Description} element found in the SSO descriptor extension of
   * the supplied entity descriptor.
   * 
   * @param ed
   *          the entity descriptor
   * @return a (possibly empty) list of {@code Description} elements
   */
  public static List<Description> getUiDescriptions(EntityDescriptor ed) {
    SSODescriptor ssoDescriptor = MetadataUtils.getSSODescriptor(ed);
    if (ssoDescriptor == null) {
      return Collections.emptyList();
    }
    Optional<UIInfo> uiInfo = getMetadataExtension(ssoDescriptor.getExtensions(), UIInfo.class);
    return uiInfo.isPresent() ? uiInfo.get().getDescriptions() : Collections.emptyList();
  }

  /**
   * Utility method that returns the {@code mdui:Description} element for the given language tag from the SSO descriptor
   * extension of the supplied entity descriptor.
   * 
   * @param ed
   *          the entity descriptor
   * @param language
   *          the language tag
   * @return the description for the given language
   */
  public static Optional<String> getUiDescription(EntityDescriptor ed, String language) {
    return getUiDescriptions(ed).stream()
      .filter(dn -> language.equals(dn.getXMLLang()))
      .map(dn -> dn.getValue())
      .findFirst();
  }

  /**
   * Returns the SSODescriptor for the supplied SP or IdP entity descriptor.
   * 
   * @param ed
   *          the entity descriptor
   * @return the SSODescriptor
   */
  private static SSODescriptor getSSODescriptor(EntityDescriptor ed) {
    if (ed.getIDPSSODescriptor(SAMLConstants.SAML20P_NS) != null) {
      return ed.getIDPSSODescriptor(SAMLConstants.SAML20P_NS);
    }
    else {
      return ed.getSPSSODescriptor(SAMLConstants.SAML20P_NS);
    }
  }

  // Hidden
  private MetadataUtils() {
  }

}
