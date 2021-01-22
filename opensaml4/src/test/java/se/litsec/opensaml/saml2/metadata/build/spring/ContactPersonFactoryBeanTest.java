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
package se.litsec.opensaml.saml2.metadata.build.spring;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.metadata.ContactPerson;
import org.opensaml.saml.saml2.metadata.ContactPersonTypeEnumeration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * Test cases for the {@code ContactPersonFactoryBean}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/factory-test.xml"})
public class ContactPersonFactoryBeanTest {
  
  @Autowired
  @Qualifier("contactPerson1")
  private ContactPerson contactPerson;
  
  @Autowired
  private Properties testProperties;
  
  @Test
  public void testFactoryFromSpringContext() throws Exception {
    Assert.assertEquals(ContactPersonTypeEnumeration.SUPPORT, this.contactPerson.getType());
    Assert.assertEquals(this.testProperties.getProperty("metadata.contactPerson.support.company"), this.contactPerson.getCompany().getValue());
    Assert.assertEquals(this.testProperties.get("metadata.contactPerson.support.givenName"), this.contactPerson.getGivenName().getValue());
    Assert.assertEquals(this.testProperties.get("metadata.contactPerson.support.surname"), this.contactPerson.getSurName().getValue());
    Assert.assertEquals(this.testProperties.get("metadata.contactPerson.support.emailAddress"), this.contactPerson.getEmailAddresses().get(0).getURI());
    Assert.assertEquals(this.testProperties.get("metadata.contactPerson.support.phone"), this.contactPerson.getTelephoneNumbers().get(0).getValue());
  }

}
