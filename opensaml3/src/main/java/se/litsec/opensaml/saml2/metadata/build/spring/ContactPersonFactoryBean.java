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
   * Assigns the type of contact person.
   * 
   * @param type
   *          the type
   * @see ContactPersonBuilder#type(ContactPersonTypeEnumeration)
   */
  public void setType(ContactPersonTypeEnumeration type) {
    this.builder.type(type);
  }

  /**
   * Assigns the {@code Company} element.
   * 
   * @param company
   *          the company
   * @see ContactPersonBuilder#company(String)
   */
  public void setCompany(String company) {
    this.builder.company(company);
  }

  /**
   * Assigns the {@code GivenName} element.
   * 
   * @param givenName
   *          the name
   * @see ContactPersonBuilder#givenName(String)
   */
  public void setGivenName(String givenName) {
    this.builder.givenName(givenName);
  }

  /**
   * Assigns the {@code SurName} element.
   * 
   * @param surname
   *          the name
   * @see ContactPersonBuilder#surname(String)
   */
  public void setSurname(String surname) {
    this.builder.surname(surname);
  }

  /**
   * Assigns the {@code EmailAddress} elements.
   * 
   * @param emailAddresses
   *          the email addresses
   * @see ContactPersonBuilder#emailAddresses(String...)
   */
  public void setEmailAddresses(List<String> emailAddresses) {
    this.builder.emailAddresses(stringListToVarArgs(emailAddresses));
  }

  /**
   * Assigns one {@code EmailAddress} element.
   * 
   * @param emailAddress
   *          the email address
   * @see ContactPersonBuilder#emailAddresses(String...)
   */
  public void setEmailAddresses(String emailAddress) {
    this.builder.emailAddresses(emailAddress);
  }

  /**
   * Assigns the {@code TelephoneNumber} elements.
   * 
   * @param telephoneNumbers
   *          the numbers to assign
   * @see ContactPersonBuilder#telephoneNumbers(String...)
   */
  public void setTelephoneNumbers(List<String> telephoneNumbers) {
    this.builder.telephoneNumbers(stringListToVarArgs(telephoneNumbers));
  }

  /**
   * Assigns one {@code TelephoneNumber} element.
   * 
   * @param telephoneNumber
   *          the number to assign
   * @see ContactPersonBuilder#telephoneNumbers(String...)
   */
  public void setTelephoneNumber(String telephoneNumber) {
    this.builder.telephoneNumbers(telephoneNumber);
  }

}
