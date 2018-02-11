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
package se.litsec.opensaml.saml2.metadata;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.ext.saml2mdui.Description;
import org.opensaml.saml.ext.saml2mdui.DisplayName;
import org.opensaml.saml.ext.saml2mdui.UIInfo;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.Extensions;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.saml.saml2.metadata.SSODescriptor;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.BasicX509Credential;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.signature.X509Data;

import se.litsec.opensaml.utils.X509CertificateUtils;

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
   * @param <T>
   *          the type of the extension
   * @return the matching extension
   */
  public static <T> Optional<T> getMetadataExtension(Extensions extensions, Class<T> clazz) {
    if (extensions == null) {
      return Optional.empty();
    }
    return extensions.getOrderedChildren()
      .stream()
      .filter(e -> clazz.isAssignableFrom(e.getClass()))
      .map(clazz::cast)
      .findFirst();
  }

  /**
   * Finds all extensions matching the supplied type.
   * 
   * @param extensions
   *          the {@link Extensions} to search
   * @param clazz
   *          the extension type
   * @param <T>
   *          the type of the extension
   * @return a (possibly empty) list of extensions elements of the given type
   */
  public static <T> List<T> getMetadataExtensions(Extensions extensions, Class<T> clazz) {
    if (extensions == null) {
      return Collections.emptyList();
    }
    return extensions.getOrderedChildren()
      .stream()
      .filter(e -> clazz.isAssignableFrom(e.getClass()))
      .map(clazz::cast)
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
    return uiInfo.map(UIInfo::getDisplayNames).orElseGet(Collections::emptyList);
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
      .map(XSString::getValue)
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
    return uiInfo.map(UIInfo::getDescriptions).orElseGet(Collections::emptyList);
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
      .map(XSString::getValue)
      .findFirst();
  }

  /**
   * Utility that extracs certificates found under the KeyDescriptor elements of a metadata record.
   * <p>
   * If {@link UsageType#SIGNING} is supplied, the method will return all certificates with usage type signing, but also
   * those that does not have a usage. And the same goes for encryption.
   * </p>
   * 
   * @param ed
   *          the metadata record
   * @param usageType
   *          the requested usage type
   * @return a list of credentials
   */
  public static List<X509Credential> getMetadataCertificates(EntityDescriptor ed, UsageType usageType) {
    SSODescriptor descriptor = getSSODescriptor(ed);
    if (descriptor == null) {
      return Collections.emptyList();
    }
    List<X509Credential> creds = new ArrayList<>();
    for (KeyDescriptor kd : descriptor.getKeyDescriptors()) {
      if (usageType.equals(kd.getUse()) || kd.getUse() == null || UsageType.UNSPECIFIED.equals(kd.getUse())) {
        if (kd.getKeyInfo() == null) {
          continue;
        }
        for (X509Data xd : kd.getKeyInfo().getX509Datas()) {
          for (org.opensaml.xmlsec.signature.X509Certificate cert : xd.getX509Certificates()) {
            try {
              creds.add(new BasicX509Credential(
                X509CertificateUtils.decodeCertificate(new ByteArrayInputStream(Base64.getDecoder().decode(cert.getValue())))));
            }
            catch (Exception e) {
            }
          }
        }
      }
    }
    return creds;
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
