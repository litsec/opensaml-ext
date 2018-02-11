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

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.saml.ext.saml2mdui.Logo;

import se.litsec.opensaml.OpenSAMLTestBase;

/**
 * Tests for {@code LogoFactoryBean}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class LogoFactoryBeanTest extends OpenSAMLTestBase {

  /**
   * Test for {@code LogoFactoryBean}.
   * 
   * @throws Exception
   *           for errors
   */
  @Test
  public void testFactory() throws Exception {
    
    String url = "http://www.litsec.se/img.jpg";
    Integer height = 100;
    Integer width = 50;

    LogoFactoryBean factory = new LogoFactoryBean(url, height, width);
    factory.afterPropertiesSet();
    
    Logo logo = factory.getObject();
    Assert.assertEquals(url, logo.getURL());
    Assert.assertNull(logo.getXMLLang());
    Assert.assertEquals(height, logo.getHeight());
    Assert.assertEquals(width, logo.getWidth());
    
    factory = new LogoFactoryBean(url, "sv", height, width);
    factory.afterPropertiesSet();
    
    logo = factory.getObject();
    Assert.assertEquals(url, logo.getURL());
    Assert.assertEquals("sv", logo.getXMLLang());
    Assert.assertEquals(height, logo.getHeight());
    Assert.assertEquals(width, logo.getWidth());
  }

  public static boolean equals(Logo l1, Logo l2) {
    if (l1 == null && l2 == null) {
      return true;
    }
    if ((l1 == null && l2 != null) || (l1 != null && l2 == null)) {
      return false;
    }
    return equalsObjects(l1.getURL(), l2.getURL()) || equalsObjects(l1.getXMLLang(), l2.getXMLLang())
        || equalsObjects(l1.getHeight(), l2.getHeight()) || equalsObjects(l1.getWidth(), l2.getWidth());
  }

  private static boolean equalsObjects(Object o1, Object o2) {
    if (o1 == null) {
      return o2 == null;
    }
    return o1.equals(o2);
  }

}
