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
package se.litsec.opensaml.xmlsec;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.EncryptedAssertion;
import org.opensaml.saml.saml2.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Test cases for {@code SAMLObjectDecrypter}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/decrypt.xml"})
public class SAMLObjectDecrypterTest {
  
  @Autowired
  private SAMLObjectDecrypter decrypter;
  
  @Test
  public void test() throws Exception {
    
    Resource r = new ClassPathResource("encrypted-20180428.xml");
    Response response = ObjectUtils.unmarshall(r.getInputStream(), Response.class);
    
    EncryptedAssertion encryptedAssertion = response.getEncryptedAssertions().get(0);
    
    Assertion assertion = decrypter.decrypt(encryptedAssertion, Assertion.class);
    Assert.assertNotNull(assertion);
    // System.out.println(SerializeSupport.prettyPrintXML(assertion.getDOM()));
  }
  
}
