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
package se.litsec.opensaml.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.saml2.core.StatusCode;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Element;

import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Test cases for {@link AbstractSAMLObjectBuilder}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AbstractSAMLObjectBuilderTest extends OpenSAMLTestBase {
  
  @Test
  public void testBuild() throws Exception {
    StatusCodeBuilder builder = new StatusCodeBuilder();
    StatusCode status = builder
        .value(StatusCode.REQUESTER_URI)
        .statusCode(
          (new StatusCodeBuilder())
            .value(StatusCode.AUTHN_FAILED_URI)
            .statusCode(StatusCode.NO_SUPPORTED_IDP_URI)
            .build())
        .build();
    
    Assert.assertEquals(StatusCode.REQUESTER_URI, status.getValue());
    Assert.assertEquals(StatusCode.AUTHN_FAILED_URI, status.getStatusCode().getValue());
    Assert.assertEquals(StatusCode.NO_SUPPORTED_IDP_URI, status.getStatusCode().getStatusCode().getValue());
    Assert.assertNull(status.getStatusCode().getStatusCode().getStatusCode());
  }
  
  @Test
  public void testBuildFromTemplate() throws Exception {
    StatusCode template = ObjectUtils.createSamlObject(StatusCode.class);
    template.setValue(StatusCode.REQUESTER_URI);
    StatusCode subCode = ObjectUtils.createSamlObject(StatusCode.class);
    subCode.setValue(StatusCode.AUTHN_FAILED_URI);
    template.setStatusCode(subCode);
    
    StatusCodeBuilder builder = new StatusCodeBuilder(template);
    StatusCode status = builder.value(StatusCode.RESPONDER_URI).build();
    
    Assert.assertEquals(StatusCode.RESPONDER_URI, status.getValue());
    Assert.assertEquals(StatusCode.AUTHN_FAILED_URI, status.getStatusCode().getValue());
    
    // Template should be untouched ...
    Assert.assertEquals(StatusCode.REQUESTER_URI, template.getValue());
    Assert.assertEquals(StatusCode.AUTHN_FAILED_URI, template.getStatusCode().getValue());
  }
  
  @Test
  public void testBuildFromResource() throws Exception {
    StatusCode template = ObjectUtils.createSamlObject(StatusCode.class);
    template.setValue(StatusCode.REQUESTER_URI);
    StatusCode subCode = ObjectUtils.createSamlObject(StatusCode.class);
    subCode.setValue(StatusCode.AUTHN_FAILED_URI);
    template.setStatusCode(subCode);
    
    // Go to XML
    Element element = ObjectUtils.marshall(template);
    String xml = XMLHelper.prettyPrintXML(element);
    
    StatusCodeBuilder builder = new StatusCodeBuilder(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    StatusCode status = builder.value(StatusCode.RESPONDER_URI).build();
    
    Assert.assertEquals(StatusCode.RESPONDER_URI, status.getValue());
    Assert.assertEquals(StatusCode.AUTHN_FAILED_URI, status.getStatusCode().getValue());
  }


  // Dummy class used for testing abstract object builder.
  //
  private static class StatusCodeBuilder extends AbstractSAMLObjectBuilder<StatusCode> {
    
    public StatusCodeBuilder() {
      super();
    }

    public StatusCodeBuilder(InputStream resource) throws XMLParserException, UnmarshallingException {
      super(resource);
    }

    public StatusCodeBuilder(StatusCode template) throws MarshallingException, UnmarshallingException {
      super(template);
    }
    
    StatusCodeBuilder value(String value) {
      this.object().setValue(value);
      return this;
    }
    
    StatusCodeBuilder statusCode(StatusCode statusCode) {
      this.object().setStatusCode(statusCode);
      return this;
    }
    
    StatusCodeBuilder statusCode(String statusCode) {
      this.object().setStatusCode((new StatusCodeBuilder()).value(statusCode).build());
      return this;
    }

    @Override
    protected Class<StatusCode> getObjectType() {
      return StatusCode.class;
    }
    
  }
  
}
