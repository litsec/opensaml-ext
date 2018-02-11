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
package se.litsec.opensaml.saml2.core.build;

import org.opensaml.saml.saml2.core.NameIDPolicy;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;

/**
 * Builder class for {@code NameIDPolicy} elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class NameIDPolicyBuilder extends AbstractSAMLObjectBuilder<NameIDPolicy> {

  /**
   * Utility method that creates a builder.
   * 
   * @return a builder
   */
  public static NameIDPolicyBuilder builder() {
    return new NameIDPolicyBuilder();
  }

  /**
   * Assigns the {@code Format} attribute to the {@code NameIDPolicy} element.
   * 
   * @param format
   *          the format URI
   * @return the builder
   */
  public NameIDPolicyBuilder format(String format) {
    this.object().setFormat(format);
    return this;
  }

  /**
   * Assigns the {@code SPNameQualifier} attribute to the {@code NameIDPolicy} element.
   * 
   * @param spNameQualifier
   *          the SP name qualifier
   * @return the builder
   */
  public NameIDPolicyBuilder spNameQualifier(String spNameQualifier) {
    this.object().setSPNameQualifier(spNameQualifier);
    return this;
  }

  /**
   * Assigns the {@code AllowCreate} attribute to the {@code NameIDPolicy} element.
   * 
   * @param allowCreate
   *          boolean flag
   * @return the builder
   */
  public NameIDPolicyBuilder allowCreate(Boolean allowCreate) {
    this.object().setAllowCreate(allowCreate);
    return this;
  }

  /** {@inheritDoc} */
  @Override
  protected Class<NameIDPolicy> getObjectType() {
    return NameIDPolicy.class;
  }

}
