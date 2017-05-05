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
package se.litsec.opensaml.saml2.attribute;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.core.xml.schema.XSBase64Binary;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeValue;

import se.litsec.opensaml.OpenSAMLTestBase;

/**
 * Tests for the {@link AttributeBuilder}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AttributeBuilderTest extends OpenSAMLTestBase {
  
  public static final String ATTRIBUTE_NAME_SN = "urn:oid:2.5.4.4";
  
  public static final String ATTRIBUTE_FRIENDLY_NAME_SN = "sn";  
  
  public static final String ATTRIBUTE_NAME_MAIL = "urn:oid:2.5.4.10";

  public static final String ATTRIBUTE_FRIENDLY_NAME_MAIL = "mail";  

  @Test
  public void testCreateStringValueAttribute() {
    Attribute attribute = AttributeBuilder.builder(ATTRIBUTE_NAME_SN)
        .friendlyName(ATTRIBUTE_FRIENDLY_NAME_SN)
        .nameFormat(Attribute.URI_REFERENCE)
        .value("Eriksson")
        .build();
    
    Assert.assertEquals(ATTRIBUTE_NAME_SN, attribute.getName());
    Assert.assertEquals(Attribute.URI_REFERENCE, attribute.getNameFormat());
    Assert.assertEquals(ATTRIBUTE_FRIENDLY_NAME_SN, attribute.getFriendlyName());
    Assert.assertTrue(attribute.getAttributeValues().size() == 1);
    Assert.assertEquals("Eriksson", AttributeUtils.getAttributeStringValue(attribute));
  }
  
  @Test
  public void testCreateMultipleStringValuesAttribute() {
    Attribute attribute = AttributeBuilder.builder(ATTRIBUTE_NAME_MAIL)
        .friendlyName(ATTRIBUTE_FRIENDLY_NAME_MAIL)
        .nameFormat(Attribute.URI_REFERENCE)
        .value("martin@litsec.se")
        .value("martin.lindstrom@litsec.se")
        .build();
    
    Assert.assertEquals(ATTRIBUTE_NAME_MAIL, attribute.getName());
    Assert.assertEquals(Attribute.URI_REFERENCE, attribute.getNameFormat());
    Assert.assertEquals(ATTRIBUTE_FRIENDLY_NAME_MAIL, attribute.getFriendlyName());
    Assert.assertEquals(Arrays.asList("martin@litsec.se", "martin.lindstrom@litsec.se"), AttributeUtils.getAttributeStringValues(attribute));
  }
  
  @Test
  public void testCreateNonStringAttribute() {
   
    // We pretend that there is a attribute that holds a boolean ...    
    XSBoolean value = AttributeBuilder.createValueObject(XSBoolean.class);
    value.setValue(XSBooleanValue.valueOf("true"));
    
    Attribute attribute = AttributeBuilder.builder("http://eid.litsec.se/types/boolean")
        .friendlyName("booleanAttribute")
        .nameFormat(Attribute.URI_REFERENCE)
        .value(value)
        .build();
    
    Assert.assertEquals("http://eid.litsec.se/types/boolean", attribute.getName());
    Assert.assertEquals(Attribute.URI_REFERENCE, attribute.getNameFormat());
    Assert.assertEquals("booleanAttribute", attribute.getFriendlyName());
    Assert.assertTrue(AttributeUtils.getAttributeValues(attribute, XSBoolean.class).size() == 1);
    Assert.assertEquals(AttributeUtils.getAttributeValue(attribute, XSBoolean.class).getValue().getValue(), Boolean.TRUE);    
  }
  
  @Test
  public void testDefaultNameFormat() {
    Attribute attribute = AttributeBuilder.builder(ATTRIBUTE_NAME_SN)
        .value("Eriksson")
        .build();
    
    Assert.assertEquals(ATTRIBUTE_NAME_SN, attribute.getName());
    Assert.assertEquals(Attribute.URI_REFERENCE, attribute.getNameFormat());
    Assert.assertEquals("Eriksson", AttributeUtils.getAttributeStringValue(attribute));
  }
  
  @Test
  public void testCreateValueObject() {
    XSBase64Binary value = AttributeBuilder.createValueObject(XSBase64Binary.class);
    Assert.assertEquals(XSBase64Binary.TYPE_NAME, value.getSchemaType());
    Assert.assertEquals(AttributeValue.DEFAULT_ELEMENT_NAME, value.getElementQName());
  }
  
  @Test
  public void testRequiredName() {
    
    try {
      new AttributeBuilder((String)null);
      Assert.fail("Expected IllegalArgumentException");
    }
    catch (IllegalArgumentException e) {      
    }
    
    AttributeBuilder builder = AttributeBuilder.builder(ATTRIBUTE_NAME_SN)
        .friendlyName(ATTRIBUTE_FRIENDLY_NAME_SN)
        .nameFormat(Attribute.URI_REFERENCE)
        .value("Eriksson");
    
    builder.name(null);
    
    try {
      builder.build();
      Assert.fail("Expected RuntimeException");
    }
    catch (RuntimeException e) {      
    }
  }

}
