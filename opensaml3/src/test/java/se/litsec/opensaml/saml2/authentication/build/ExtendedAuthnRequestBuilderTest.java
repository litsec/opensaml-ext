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
package se.litsec.opensaml.saml2.authentication.build;

import java.io.IOException;
import java.time.LocalDateTime;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.component.ComponentInitializationException;
import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.saml2.metadata.provider.MetadataProvider;
import se.litsec.opensaml.saml2.metadata.provider.spring.SpringResourceMetadataProvider;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Test cases for {@code ExtendedAuthnRequestBuilder}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ExtendedAuthnRequestBuilderTest extends OpenSAMLTestBase {
  
  private static final String TEST_IDP = "https://idp.svelegtest.se/idp";
  private static final String TEST_SP = "https://eid.svelegtest.se/validation/testsp1";
  
  private static MetadataProvider metadataProvider;
  
  @BeforeClass
  public static void setup() throws IOException, ComponentInitializationException {    
    metadataProvider = new SpringResourceMetadataProvider(new ClassPathResource("/metadata/sveleg-fedtest.xml"));
    metadataProvider.initialize();
  }
  
  @Test
  public void testBuildAuthnRequest() throws Exception {
    final EntityDescriptor spMetadata = metadataProvider.getEntityDescriptor(TEST_SP).orElse(null);
    final EntityDescriptor idpMetadata = metadataProvider.getEntityDescriptor(TEST_IDP).orElse(null);
    
    ExtendedAuthnRequestBuilder builder = new ExtendedAuthnRequestBuilder(spMetadata, idpMetadata);
    
    AuthnRequest request = builder
        .nameIDPolicyFormat(NameID.TRANSIENT)
        .binding(SAMLConstants.SAML2_POST_BINDING_URI)
        .assignDefaults()
        .authnContextClassRefs(true, false, "http://id.elegnamnden.se/loa/1.0/loa3", "http://id.elegnamnden.se/loa/1.0/loa4", "http://id.elegnamnden.se/loa/1.0/loa100")
        .issueInstant(LocalDateTime.now())
        .build();
    
    Element elm = ObjectUtils.marshall(request);
    System.out.println(SerializeSupport.prettyPrintXML(elm));
    
  }

}
