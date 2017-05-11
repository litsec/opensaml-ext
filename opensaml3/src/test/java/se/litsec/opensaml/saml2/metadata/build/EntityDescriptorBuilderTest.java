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

import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.ext.idpdisco.DiscoveryResponse;
import org.opensaml.saml.ext.saml2mdattr.EntityAttributes;
import org.opensaml.saml.ext.saml2mdui.Logo;
import org.opensaml.saml.ext.saml2mdui.UIInfo;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.metadata.AssertionConsumerService;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;
import org.opensaml.security.credential.UsageType;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.core.LocalizedString;
import se.litsec.opensaml.saml2.attribute.AttributeUtils;
import se.litsec.opensaml.saml2.metadata.MetadataUtils;
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
        new LocalizedString(
          "E-legitimationsnämndens e-tjänst (Service Provider) för test- och referensändamål - Konfigurerad for legitimering enligt Svensk e-legitimation/eIDAS",
          "sv"),
        new LocalizedString(
          "The Swedish e-Identification Board Service Provider for test and reference - Configured for authentication according to Swedish eID/eIDAS",
          "en")
    };

    final Logo[] uiLogos = {
        LogoBuilder.logo("https://eid.litsec.se/svelegtest-sp/img/elegnamnden_114x100.png", 100, 114),
        LogoBuilder.logo("https://eid.litsec.se/svelegtest-sp/img/elegnamnden_logo_160x90.png", 90, 160),
        LogoBuilder.logo("https://eid.litsec.se/svelegtest-sp/img/elegnamnden_notext_16x16.png", 16, 16),
        LogoBuilder.logo("https://eid.litsec.se/svelegtest-sp/img/elegnamnden_notext_68x67.png", 67, 68)
    };

    final String[] discoveryResponses = { "https://eid.litsec.se/svelegtest-sp/authnrequest/disco/11",
        "https://localhost:8443/svelegtest-sp/authnrequest/disco/11" };

    final Resource signatureCertificateResource = new ClassPathResource("/credentials/litsec_sign.crt");
    final X509Certificate encryptionCertificate = X509CertificateUtils.decodeCertificate((new ClassPathResource(
      "/credentials/litsec_auth.crt")).getInputStream());

    final String[] nameIDFormats = { NameID.PERSISTENT, NameID.TRANSIENT };

    final AssertionConsumerService[] assertionConsumerServices = {
        AssertionConsumerServiceBuilder.builder()
          .postBinding()
          .isDefault(true)
          .index(0)
          .location("https://eid.litsec.se/svelegtest-sp/saml2/post/11")
          .build(),
        AssertionConsumerServiceBuilder.builder()
          .postBinding()
          .index(1)
          .location("https://localhost:8443/svelegtest-sp/saml2/post/11")
          .build()
    };

    final List<String> requestedAttributes = 
        Arrays.asList("urn:oid:1.2.752.29.4.13", "urn:oid:1.2.752.201.3.7", "urn:oid:1.2.752.201.3.4");
      
    final LocalizedString[] serviceNames = {
        new LocalizedString("E-legitimationsnämndens Test-SP för eIDAS", "sv"),
        new LocalizedString("The e-Identification Board Test SP for eIDAS", Locale.ENGLISH)
    };

    final LocalizedString[] organizationNames = {
        new LocalizedString("E-legitimationsnämnden", "sv"), new LocalizedString("Swedish e-Identification Board", "en")
    };
    
    final LocalizedString[] organizationDisplayNames = {
        new LocalizedString("E-legitimationsnämnden", "sv"), new LocalizedString("Swedish e-Identification Board", "en")
    };
    
    final LocalizedString organizationURL = new LocalizedString("http://www.elegnamnden.se", "sv");

    final ContactPerson contactPersonTemplate = ContactPersonBuilder.builder()
      .company("Litsec AB")
      .givenName("Martin")
      .surname("Lindström")
      .emailAddresses("martin.lindstrom@litsec.se")
      .telephoneNumbers("+46 (0)70 361 98 80")
      .build();

    SpEntityDescriptorBuilder builder = new SpEntityDescriptorBuilder();

    EntityDescriptor ed = builder.entityID(SP_ENTITY_ID)
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
      .assertionConsumerService(assertionConsumerServices)
      .attributeConsumingService(
        AttributeConsumingServiceBuilder.builder()
          .isDefault(true)
          .index(0)
          .serviceNames(serviceNames)
          .requestedAttributes(requestedAttributes.stream()
            .map(a -> RequestedAttributeBuilder.builder(a).isRequired(false).build())
            .collect(Collectors.toList()))
          .build())
      .organization(
        OrganizationBuilder.builder()
          .organizationNames(organizationNames)
          .organizationDisplayNames(organizationDisplayNames)
          .organizationURLs(organizationURL)
          .build())
      .contactPersons(
        ContactPersonBuilder.builder(contactPersonTemplate).type(ContactPersonTypeEnumeration.TECHNICAL).build(),
        ContactPersonBuilder.builder(contactPersonTemplate).type(ContactPersonTypeEnumeration.SUPPORT).build())
      .build();

    Element elm = ObjectUtils.marshall(ed);
    System.out.println(SerializeSupport.prettyPrintXML(elm));

    Assert.assertEquals(metadataId, ed.getID());
    Assert.assertEquals((Long) cacheDuration, ed.getCacheDuration());
    Assert.assertEquals(validUntil.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), ed.getValidUntil().getMillis());

    Optional<EntityAttributes> entityAttributes = MetadataUtils.getEntityAttributes(ed);
    Assert.assertTrue(entityAttributes.isPresent());
    Optional<Attribute> entityCategoryAttribute = AttributeUtils.getAttribute(
      AbstractEntityDescriptorBuilder.ENTITY_CATEGORY_ATTRIBUTE_NAME, entityAttributes.get().getAttributes());
    Assert.assertTrue(entityCategoryAttribute.isPresent());
    Assert.assertEquals(Arrays.asList(entityCategories), AttributeUtils.getAttributeStringValues(entityCategoryAttribute.get()));

    SPSSODescriptor ssoDescriptor = ed.getSPSSODescriptor(SAMLConstants.SAML20P_NS);
    
    Assert.assertTrue(ssoDescriptor.isAuthnRequestsSigned());
    Assert.assertTrue(ssoDescriptor.getWantAssertionsSigned());
    
    Optional<UIInfo> uiInfo = MetadataUtils.getMetadataExtension(ssoDescriptor.getExtensions(), UIInfo.class);
    Assert.assertTrue(uiInfo.isPresent());
    Assert.assertEquals(Arrays.asList(uiDisplayNames), 
      uiInfo.get().getDisplayNames().stream().map(dn -> new LocalizedString(dn.getValue(), dn.getXMLLang())).collect(Collectors.toList()));
    Assert.assertEquals(Arrays.asList(uiDescriptions), 
      uiInfo.get().getDescriptions().stream().map(d -> new LocalizedString(d.getValue(), d.getXMLLang())).collect(Collectors.toList()));
    // The Logo class is buggy. Its hashcode assumes that there is a language set, so we'll have to check all attributes.
    Assert.assertEquals(Arrays.asList(uiLogos).stream().map(l -> l.getURL()).collect(Collectors.toList()), 
      uiInfo.get().getLogos().stream().map(l -> l.getURL()).collect(Collectors.toList()));
    Assert.assertEquals(Arrays.asList(uiLogos).stream().map(l -> l.getHeight()).collect(Collectors.toList()), 
      uiInfo.get().getLogos().stream().map(l -> l.getHeight()).collect(Collectors.toList()));
    Assert.assertEquals(Arrays.asList(uiLogos).stream().map(l -> l.getWidth()).collect(Collectors.toList()), 
      uiInfo.get().getLogos().stream().map(l -> l.getWidth()).collect(Collectors.toList()));
    
    List<DiscoveryResponse> discoResponses = MetadataUtils.getMetadataExtensions(ssoDescriptor.getExtensions(), DiscoveryResponse.class);
    Assert.assertTrue(discoResponses.size() == 2);
    Assert.assertEquals(1, (int) discoResponses.get(0).getIndex());
    Assert.assertEquals(discoveryResponses[0], discoResponses.get(0).getLocation());
    Assert.assertEquals(2, (int) discoResponses.get(1).getIndex());
    Assert.assertEquals(discoveryResponses[1], discoResponses.get(1).getLocation());
    
    Assert.assertEquals(UsageType.SIGNING, ssoDescriptor.getKeyDescriptors().get(0).getUse());
    Assert.assertEquals("Litsec Signing", ssoDescriptor.getKeyDescriptors().get(0).getKeyInfo().getKeyNames().get(0).getValue());
    Assert.assertTrue(ssoDescriptor.getKeyDescriptors().get(0).getKeyInfo().getX509Datas().get(0).getX509Certificates().size() == 1);
    
    Assert.assertEquals(UsageType.ENCRYPTION, ssoDescriptor.getKeyDescriptors().get(1).getUse());
    Assert.assertEquals("Litsec Encrypt", ssoDescriptor.getKeyDescriptors().get(1).getKeyInfo().getKeyNames().get(0).getValue());
    Assert.assertTrue(ssoDescriptor.getKeyDescriptors().get(1).getKeyInfo().getX509Datas().get(0).getX509Certificates().size() == 1);
   
    Assert.assertEquals(Arrays.asList(nameIDFormats), ssoDescriptor.getNameIDFormats().stream().map(n -> n.getFormat()).collect(Collectors.toList()));
    
    Assert.assertEquals(2, ssoDescriptor.getAssertionConsumerServices().size());
    
    Assert.assertEquals(Boolean.TRUE, ssoDescriptor.getAssertionConsumerServices().get(0).isDefault());
    Assert.assertNull(ssoDescriptor.getAssertionConsumerServices().get(1).isDefaultXSBoolean());
    Assert.assertEquals(SAMLConstants.SAML2_POST_BINDING_URI, ssoDescriptor.getAssertionConsumerServices().get(0).getBinding());
    Assert.assertEquals(SAMLConstants.SAML2_POST_BINDING_URI, ssoDescriptor.getAssertionConsumerServices().get(1).getBinding());
    Assert.assertEquals(0, (int) ssoDescriptor.getAssertionConsumerServices().get(0).getIndex());
    Assert.assertEquals(1, (int) ssoDescriptor.getAssertionConsumerServices().get(1).getIndex());
    Assert.assertEquals("https://eid.litsec.se/svelegtest-sp/saml2/post/11", ssoDescriptor.getAssertionConsumerServices().get(0).getLocation());
    Assert.assertEquals("https://localhost:8443/svelegtest-sp/saml2/post/11", ssoDescriptor.getAssertionConsumerServices().get(1).getLocation());
    
    Assert.assertEquals(requestedAttributes, 
      ssoDescriptor.getAttributeConsumingServices().get(0).getRequestAttributes().stream().map(a -> a.getName()).collect(Collectors.toList()));    
    Assert.assertEquals(Arrays.asList(serviceNames), 
      ssoDescriptor.getAttributeConsumingServices().get(0).getNames().stream().map(s -> new LocalizedString(s.getValue(), s.getXMLLang())).collect(Collectors.toList()));
    
    Assert.assertEquals(Arrays.asList(organizationNames), 
      ed.getOrganization().getOrganizationNames().stream().map(n -> new LocalizedString(n.getValue(), n.getXMLLang())).collect(Collectors.toList())); 
    Assert.assertEquals(Arrays.asList(organizationDisplayNames), 
      ed.getOrganization().getDisplayNames().stream().map(n -> new LocalizedString(n.getValue(), n.getXMLLang())).collect(Collectors.toList()));
    Assert.assertEquals(Arrays.asList(organizationURL), 
      ed.getOrganization().getURLs().stream().map(n -> new LocalizedString(n.getValue(), n.getXMLLang())).collect(Collectors.toList()));

    Assert.assertEquals(2, ed.getContactPersons().size());
    Assert.assertEquals(ContactPersonTypeEnumeration.TECHNICAL, ed.getContactPersons().get(0).getType());
    Assert.assertEquals(ContactPersonTypeEnumeration.SUPPORT, ed.getContactPersons().get(1).getType());
    for (int i = 0; i < 2; i++) {
      Assert.assertEquals("Litsec AB", ed.getContactPersons().get(i).getCompany().getName());
      Assert.assertEquals("Martin", ed.getContactPersons().get(i).getGivenName().getName());
      Assert.assertEquals("Lindström", ed.getContactPersons().get(i).getSurName().getName());
      Assert.assertEquals(Arrays.asList("martin.lindstrom@litsec.se"), 
        ed.getContactPersons().get(i).getEmailAddresses().stream().map(m -> m.getAddress()).collect(Collectors.toList()));
      Assert.assertEquals(Arrays.asList("+46 (0)70 361 98 80"),
        ed.getContactPersons().get(i).getTelephoneNumbers().stream().map(t -> t.getNumber()).collect(Collectors.toList()));
    }
  }
  

}
