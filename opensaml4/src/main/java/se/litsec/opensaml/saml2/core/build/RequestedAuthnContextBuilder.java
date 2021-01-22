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

import java.util.Arrays;
import java.util.List;

import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnContextDeclRef;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;

/**
 * A builder for {@code RequestedAuthnContext} elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class RequestedAuthnContextBuilder extends AbstractSAMLObjectBuilder<RequestedAuthnContext> {

  /**
   * Utility method that creates a builder.
   * 
   * @return a builder
   */
  public static RequestedAuthnContextBuilder builder() {
    return new RequestedAuthnContextBuilder();
  }

  /**
   * Assigns the {@code Comparison} attribute to the {@code RequestedAuthnContext} object.
   * 
   * @param type
   *          the type of comparison
   * @return the builder
   */
  public RequestedAuthnContextBuilder comparison(AuthnContextComparisonTypeEnumeration type) {
    this.object().setComparison(type);
    return this;
  }

  /**
   * Assigns {@code AuthnContextClassRef} elements to the {@code RequestedAuthnContext} object.
   * 
   * @param classRefs
   *          authentication context class references
   * @return the builder
   */
  public RequestedAuthnContextBuilder authnContextClassRefs(List<String> classRefs) {
    if (classRefs == null || classRefs.isEmpty()) {
      this.object().getAuthnContextClassRefs().clear();
    }
    else {
      for (String cr : classRefs) {
        AuthnContextClassRef accr = (AuthnContextClassRef) XMLObjectSupport.buildXMLObject(AuthnContextClassRef.DEFAULT_ELEMENT_NAME); 
        accr.setURI(cr);
        this.object().getAuthnContextClassRefs().add(accr);
      }
    }
    return this;
  }

  /**
   * @see #authnContextClassRefs(List)
   * @param classRefs
   *          authentication context class references
   * @return the builder
   */
  public RequestedAuthnContextBuilder authnContextClassRefs(String... classRefs) {
    return this.authnContextClassRefs(classRefs != null ? Arrays.asList(classRefs) : null);
  }

  /**
   * Assigns {@code AuthnContextDeclRef} elements to the {@code RequestedAuthnContext} object.
   * 
   * @param declRefs
   *          authentication context declaration references
   * @return the builder
   */
  public RequestedAuthnContextBuilder authnContextDeclRefs(List<String> declRefs) {
    if (declRefs == null || declRefs.isEmpty()) {
      this.object().getAuthnContextDeclRefs().clear();
    }
    else {
      for (String dr : declRefs) {
        AuthnContextDeclRef acdr = (AuthnContextDeclRef) XMLObjectSupport.buildXMLObject(AuthnContextDeclRef.DEFAULT_ELEMENT_NAME); 
        acdr.setURI(dr);
        this.object().getAuthnContextDeclRefs().add(acdr);
      }
    }
    return this;
  }

  /**
   * @see #authnContextDeclRefs(List)
   * 
   * @param declRefs
   *          authentication context declaration references
   * @return the builder
   */
  public RequestedAuthnContextBuilder authnContextDeclRefs(String... declRefs) {
    return this.authnContextDeclRefs(declRefs != null ? Arrays.asList(declRefs) : null);
  }

  /** {@inheritDoc} */
  @Override
  protected Class<RequestedAuthnContext> getObjectType() {
    return RequestedAuthnContext.class;
  }

}
