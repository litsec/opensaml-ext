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
package se.litsec.opensaml.saml2.authentication.build;

import java.io.IOException;
import java.time.Instant;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opensaml.core.xml.util.XMLObjectSupport;
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
    final EntityDescriptor spMetadata = metadataProvider.getEntityDescriptor(TEST_SP);
    final EntityDescriptor idpMetadata = metadataProvider.getEntityDescriptor(TEST_IDP);
    
    ExtendedAuthnRequestBuilder builder = new ExtendedAuthnRequestBuilder(spMetadata, idpMetadata);
    
    AuthnRequest request = builder
        .nameIDPolicyFormat(NameID.TRANSIENT)
        .binding(SAMLConstants.SAML2_POST_BINDING_URI)
        .assignDefaults()
        .authnContextClassRefs(true, false, "http://id.elegnamnden.se/loa/1.0/loa3", "http://id.elegnamnden.se/loa/1.0/loa4", "http://id.elegnamnden.se/loa/1.0/loa100")
        .issueInstant(Instant.now())
        .build();
    
    Element elm = XMLObjectSupport.marshall(request);
    System.out.println(SerializeSupport.prettyPrintXML(elm));
    
  }

}
