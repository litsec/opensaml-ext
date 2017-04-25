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

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;

import org.junit.Test;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.NameIDFormat;
import org.opensaml.security.credential.UsageType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.core.LocalizedString;
import se.litsec.opensaml.utils.ObjectUtils;
import se.litsec.opensaml.utils.X509CertificateUtils;

/**
 * Test cases for {@link AbstractEntityDescriptorBuilder}, {@link SpEntityDescriptorBuilder} and
 * {@link IdpEntityDescriptorBuilder}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class EntityDescriptorBuilderTest extends OpenSAMLTestBase {

  private static final String SP_ENTITY_ID = "https://eid.litsec.se/sp/eidas";

  /**
   * Tests building an {@code EntityDescriptor} for a Service Provider from scratch. 
   */
  @Test
  public void testBuildSpMetadata() throws Exception {

    final String metadataId = "MLoeU7ALIHTZ61ibqZdJ";
    LocalDateTime validUntil = LocalDateTime.now();
    validUntil.plusDays(7);
    long cacheDuration = 3600000L;

    final String[] entityCategories = { "http://id.elegnamnden.se/ec/1.0/loa3-pnr", "http://id.elegnamnden.se/ec/1.0/eidas-naturalperson" };

    final LocalizedString[] uiDisplayNames = {
        new LocalizedString("E-legitimationsnämndens Test-SP för eIDAS", "sv"),
        new LocalizedString("The e-Identification Board Test SP for eIDAS", "en")
    };
    
    final LocalizedString[] uiDescriptions = {
        new LocalizedString("E-legitimationsnämndens e-tjänst (Service Provider) för test- och referensändamål - Konfigurerad for legitimering enligt Svensk e-legitimation/eIDAS", "sv"),
        new LocalizedString("The Swedish e-Identification Board Service Provider for test and reference - Configured for authentication according to Swedish eID/eIDAS", "en")
    };
    
    final Logo[] uiLogos = {
      LogoBuilder.logo("https://eid.litsec.se/svelegtest-sp/img/elegnamnden_114x100.png", 100, 114),
      LogoBuilder.logo("https://eid.litsec.se/svelegtest-sp/img/elegnamnden_logo_160x90.png", 90, 160),
      LogoBuilder.logo("https://eid.litsec.se/svelegtest-sp/img/elegnamnden_notext_16x16.png", 16, 16),
      LogoBuilder.logo("https://eid.litsec.se/svelegtest-sp/img/elegnamnden_notext_68x67.png", 67, 68)
    };
    
    final String[] discoveryResponses = { "https://eid.litsec.se/svelegtest-sp/authnrequest/disco/11", "https://localhost:8443/svelegtest-sp/authnrequest/disco/11" };
    
    final Resource signatureCertificateResource = new ClassPathResource("/credentials/litsec_sign.crt");
    final X509Certificate encryptionCertificate = 
        X509CertificateUtils.decodeCertificate((new ClassPathResource("/credentials/litsec_auth.crt")).getInputStream());
    
    final String[] nameIDFormats = { NameID.PERSISTENT, NameID.TRANSIENT };
    
    final AssertionConsumerService[] assertionConsumerServices = {
        AssertionConsumerServiceBuilder.builder().postBinding().isDefault(true).index(0).location("https://eid.litsec.se/svelegtest-sp/saml2/post/11").build(),
        AssertionConsumerServiceBuilder.builder().postBinding().index(1).location("https://localhost:8443/svelegtest-sp/saml2/post/11").build()
    };

    SpEntityDescriptorBuilder builder = new SpEntityDescriptorBuilder();

    builder = builder.entityID(SP_ENTITY_ID)
      .id(metadataId)
      .cacheDuration(cacheDuration)
      .validUntil(validUntil)
      .entityCategories(entityCategories)
      .authnRequestsSigned(true)
      .wantAssertionsSigned(true)
      .uiInfoExtension(
        UIInfoBuilder.builder()
          .displayNames(uiDisplayNames)
          .descriptions(uiDescriptions)
          .logos(uiLogos)
          .build())
      .discoveryResponse(discoveryResponses)
      .keyDescriptors(
        KeyDescriptorBuilder.builder()
          .use(UsageType.SIGNING)
          .keyName("Litsec Signing")
          .certificate(signatureCertificateResource.getInputStream())
          .build(),
        KeyDescriptorBuilder.builder()
          .use(UsageType.ENCRYPTION)
          .keyName("Litsec Encrypt")
          .certificate(encryptionCertificate)
          .build())
      .nameIDFormats(nameIDFormats)
      .assertionConsumerService(assertionConsumerServices);
    
    // TODO: complete

    EntityDescriptor ed = builder.build();
    Element elm = ObjectUtils.marshall(ed);
    System.out.println(SerializeSupport.prettyPrintXML(elm));
  }

}
