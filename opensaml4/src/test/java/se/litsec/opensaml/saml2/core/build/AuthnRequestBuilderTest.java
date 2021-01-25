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
package se.litsec.opensaml.saml2.core.build;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.core.NameID;

import se.litsec.opensaml.OpenSAMLTestBase;

/**
 * Test cases for the {@code AuthnRequestBuilder}.
 */
public class AuthnRequestBuilderTest extends OpenSAMLTestBase {

  @Test
  public void testBuildAuthnRequest() throws Exception {

    final String assertionConsumerServiceURL = "https://eid.litsec.se/svelegtest-sp/saml2/post/11";
    final String destination = "https://idp.svelegtest.se/idp/profile/SAML2/Redirect/SSO";
    final String id = "_4ugrqo5Eg4kFVBSgl6MHd1HqY2Dj35NY8HS2ut9H";
    final String issuer = "https://eid.litsec.se/sp/eidas";
    final String authnContext = "http://id.elegnamnden.se/loa/1.0/loa3";
    final Instant now = Instant.now();
    final String requesterID = "http://www.example.com/sp";

    AuthnRequest request = AuthnRequestBuilder.builder()
      .assertionConsumerServiceURL(assertionConsumerServiceURL)
      .destination(destination)
      .forceAuthn(true)
      .isPassive(false)
      .id(id)
      .issueInstant(now)
      .postProtocolBinding()
      .issuer(issuer)
      .nameIDPolicy(
        NameIDPolicyBuilder.builder().allowCreate(true).format(NameID.PERSISTENT).build())
      .scoping(
        ScopingBuilder.builder().requesterIDs(requesterID).build())
      .requestedAuthnContext(RequestedAuthnContextBuilder.builder()
        .comparison(AuthnContextComparisonTypeEnumeration.EXACT)
        .authnContextClassRefs(authnContext)
        .build())
      .build();

    Assert.assertEquals(id, request.getID());
    Assert.assertEquals(assertionConsumerServiceURL, request.getAssertionConsumerServiceURL());
    Assert.assertEquals(destination, request.getDestination());
    Assert.assertEquals(issuer, request.getIssuer().getValue());
    Assert.assertEquals(NameID.ENTITY, request.getIssuer().getFormat());
    Assert.assertEquals(Boolean.TRUE, request.isForceAuthn());
    Assert.assertEquals(Boolean.FALSE, request.isPassive());
    Assert.assertEquals(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), request.getIssueInstant().toEpochMilli());
    Assert.assertEquals(SAMLConstants.SAML2_POST_BINDING_URI, request.getProtocolBinding());
    Assert.assertEquals(NameID.PERSISTENT, request.getNameIDPolicy().getFormat());
    Assert.assertEquals(requesterID, request.getScoping().getRequesterIDs().get(0).getURI());
    Assert.assertEquals(Boolean.TRUE, request.getNameIDPolicy().getAllowCreate());
    Assert.assertEquals(AuthnContextComparisonTypeEnumeration.EXACT, request.getRequestedAuthnContext().getComparison());
    Assert.assertEquals(Arrays.asList(authnContext),
      request.getRequestedAuthnContext().getAuthnContextClassRefs().stream()
        .map(AuthnContextClassRef::getURI)
        .collect(Collectors.toList()));

  }

}
