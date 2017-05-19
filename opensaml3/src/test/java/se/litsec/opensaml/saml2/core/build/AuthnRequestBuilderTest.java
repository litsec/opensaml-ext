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
package se.litsec.opensaml.saml2.core.build;

import java.time.LocalDateTime;
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
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.utils.ObjectUtils;

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
    final LocalDateTime now = LocalDateTime.now();

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
      .requestedAuthnContext(RequestedAuthnContextBuilder.builder()
        .comparison(AuthnContextComparisonTypeEnumeration.EXACT)
        .authnContextClassRefs(authnContext)
        .build())
      .build();

    Element elm = ObjectUtils.marshall(request);
    System.out.println(SerializeSupport.prettyPrintXML(elm));

    Assert.assertEquals(id, request.getID());
    Assert.assertEquals(assertionConsumerServiceURL, request.getAssertionConsumerServiceURL());
    Assert.assertEquals(destination, request.getDestination());
    Assert.assertEquals(issuer, request.getIssuer().getValue());
    Assert.assertEquals(NameID.ENTITY, request.getIssuer().getFormat());
    Assert.assertEquals(Boolean.TRUE, request.isForceAuthn());
    Assert.assertEquals(Boolean.FALSE, request.isPassive());
    Assert.assertEquals(now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), request.getIssueInstant().getMillis());
    Assert.assertEquals(SAMLConstants.SAML2_POST_BINDING_URI, request.getProtocolBinding());
    Assert.assertEquals(NameID.PERSISTENT, request.getNameIDPolicy().getFormat());
    Assert.assertEquals(Boolean.TRUE, request.getNameIDPolicy().getAllowCreate());
    Assert.assertEquals(AuthnContextComparisonTypeEnumeration.EXACT, request.getRequestedAuthnContext().getComparison());
    Assert.assertEquals(Arrays.asList(authnContext),
      request.getRequestedAuthnContext().getAuthnContextClassRefs().stream()
        .map(AuthnContextClassRef::getAuthnContextClassRef)
        .collect(Collectors.toList()));

  }

}
