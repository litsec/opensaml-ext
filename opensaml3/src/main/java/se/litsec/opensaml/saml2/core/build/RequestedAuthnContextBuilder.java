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

import java.util.Arrays;
import java.util.List;

import org.opensaml.saml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml.saml2.core.AuthnContextDeclRef;
import org.opensaml.saml.saml2.core.RequestedAuthnContext;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.utils.ObjectUtils;

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
        AuthnContextClassRef accr = ObjectUtils.createSamlObject(AuthnContextClassRef.class);
        accr.setAuthnContextClassRef(cr);
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
        AuthnContextDeclRef acdr = ObjectUtils.createSamlObject(AuthnContextDeclRef.class);
        acdr.setAuthnContextDeclRef(dr);
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
