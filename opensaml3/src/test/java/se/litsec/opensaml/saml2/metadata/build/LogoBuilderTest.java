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
package se.litsec.opensaml.saml2.metadata.build;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.saml.ext.saml2mdui.Logo;

import se.litsec.opensaml.OpenSAMLTestBase;

/**
 * Tests for {@code LogoBuilder}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class LogoBuilderTest extends OpenSAMLTestBase {

  @Test
  public void testBuild() {
    String url = "http://www.litsec.se/img.jpg";
    Integer height = 100;
    Integer width = 50;
    
    Logo logo = LogoBuilder.builder().url(url).height(height).width(width).build();

    Assert.assertEquals(url, logo.getURL());
    Assert.assertNull(logo.getXMLLang());
    Assert.assertEquals(height, logo.getHeight());
    Assert.assertEquals(width, logo.getWidth());
        
    logo = LogoBuilder.builder().url(url).language("sv").height(height).width(width).build();
    
    Assert.assertEquals(url, logo.getURL());
    Assert.assertEquals("sv", logo.getXMLLang());
    Assert.assertEquals(height, logo.getHeight());
    Assert.assertEquals(width, logo.getWidth());
  }
  
}
