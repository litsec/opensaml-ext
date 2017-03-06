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
