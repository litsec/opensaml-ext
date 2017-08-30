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
package se.litsec.opensaml.saml2.metadata.build.spring;

import java.util.List;

import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.ContactPersonBuilder;

/**
 * A Spring factory bean for creating {@link ContactPerson} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see ContactPersonBuilder
 */
public class ContactPersonFactoryBean extends AbstractSAMLObjectBuilderFactoryBean<ContactPerson> {

  /** The builder. */
  private ContactPersonBuilder builder;

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return ContactPerson.class;
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<ContactPerson> builder() {
    return this.builder;
  }
  
  /**
   * @see ContactPersonBuilder#type(ContactPersonTypeEnumeration)
   */
  public void setType(ContactPersonTypeEnumeration type) {
    this.builder.type(type);
  }

  /**
   * @see ContactPersonBuilder#company(String)
   */
  public void setCompany(String company) {
    this.builder.company(company);
  }

  /**
   * @see ContactPersonBuilder#givenName(String)
   */
  public void setGivenName(String givenName) {
    this.builder.givenName(givenName);
  }

  /**
   * @see ContactPersonBuilder#surname(String)
   */
  public void setSurname(String surname) {
    this.builder.surname(surname);
  }

  /**
   * @see ContactPersonBuilder#emailAddresses(String...)
   */
  public void setEmailAddresses(List<String> emailAddresses) {
    this.builder.emailAddresses(stringListToVarArgs(emailAddresses));
  }

  /**
   * @see ContactPersonBuilder#emailAddresses(String...)
   */
  public void setEmailAddresses(String emailAddress) {
    this.builder.emailAddresses(emailAddress);
  }

  /**
   * @see ContactPersonBuilder#telephoneNumbers(String...)
   */
  public void setTelephoneNumbers(List<String> telephoneNumbers) {
    this.builder.telephoneNumbers(stringListToVarArgs(telephoneNumbers));
  }

  /**
   * @see ContactPersonBuilder#telephoneNumbers(String...)
   */
  public void setTelephoneNumber(String telephoneNumber) {
    this.builder.telephoneNumbers(telephoneNumber);
  }

}
