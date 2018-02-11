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
package se.litsec.opensaml.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.core.StatusCode;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.xml.SerializeSupport;
import net.shibboleth.utilities.java.support.xml.XMLParserException;
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
        .value(StatusCode.REQUESTER)
        .statusCode(
          (new StatusCodeBuilder())
            .value(StatusCode.AUTHN_FAILED)
            .statusCode(StatusCode.NO_SUPPORTED_IDP)
            .build())
        .build();
    
    Assert.assertEquals(StatusCode.REQUESTER, status.getValue());
    Assert.assertEquals(StatusCode.AUTHN_FAILED, status.getStatusCode().getValue());
    Assert.assertEquals(StatusCode.NO_SUPPORTED_IDP, status.getStatusCode().getStatusCode().getValue());
    Assert.assertNull(status.getStatusCode().getStatusCode().getStatusCode());
  }
  
  @Test
  public void testBuildFromTemplate() throws Exception {
    StatusCode template = ObjectUtils.createSamlObject(StatusCode.class);
    template.setValue(StatusCode.REQUESTER);
    StatusCode subCode = ObjectUtils.createSamlObject(StatusCode.class);
    subCode.setValue(StatusCode.AUTHN_FAILED);
    template.setStatusCode(subCode);
    
    StatusCodeBuilder builder = new StatusCodeBuilder(template);
    StatusCode status = builder.value(StatusCode.RESPONDER).build();
    
    Assert.assertEquals(StatusCode.RESPONDER, status.getValue());
    Assert.assertEquals(StatusCode.AUTHN_FAILED, status.getStatusCode().getValue());
    
    // Template should be untouched ...
    Assert.assertEquals(StatusCode.REQUESTER, template.getValue());
    Assert.assertEquals(StatusCode.AUTHN_FAILED, template.getStatusCode().getValue());
  }
  
  @Test
  public void testBuildFromResource() throws Exception {
    StatusCode template = ObjectUtils.createSamlObject(StatusCode.class);
    template.setValue(StatusCode.REQUESTER);
    StatusCode subCode = ObjectUtils.createSamlObject(StatusCode.class);
    subCode.setValue(StatusCode.AUTHN_FAILED);
    template.setStatusCode(subCode);
    
    // Go to XML
    Element element = ObjectUtils.marshall(template);
    String xml = SerializeSupport.prettyPrintXML(element);
    
    StatusCodeBuilder builder = new StatusCodeBuilder(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    StatusCode status = builder.value(StatusCode.RESPONDER).build();
    
    Assert.assertEquals(StatusCode.RESPONDER, status.getValue());
    Assert.assertEquals(StatusCode.AUTHN_FAILED, status.getStatusCode().getValue());
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
