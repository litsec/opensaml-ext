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
