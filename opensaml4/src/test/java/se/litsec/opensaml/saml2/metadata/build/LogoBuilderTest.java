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

    Assert.assertEquals(url, logo.getURI());
    Assert.assertNull(logo.getXMLLang());
    Assert.assertEquals(height, logo.getHeight());
    Assert.assertEquals(width, logo.getWidth());
        
    logo = LogoBuilder.builder().url(url).language("sv").height(height).width(width).build();
    
    Assert.assertEquals(url, logo.getURI());
    Assert.assertEquals("sv", logo.getXMLLang());
    Assert.assertEquals(height, logo.getHeight());
    Assert.assertEquals(width, logo.getWidth());
  }
  
}
