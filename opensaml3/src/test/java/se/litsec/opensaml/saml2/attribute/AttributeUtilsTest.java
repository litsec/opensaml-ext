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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.saml2.core.Attribute;

import se.litsec.opensaml.OpenSAMLTestBase;

/**
 * Test cases for {@link AttributeUtils}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AttributeUtilsTest extends OpenSAMLTestBase {
  
  @Test
  public void testGetAttributeStringValues() {
    
    Attribute attribute = AttributeBuilder.builder(AttributeBuilderTest.ATTRIBUTE_NAME_MAIL)
        .friendlyName(AttributeBuilderTest.ATTRIBUTE_FRIENDLY_NAME_MAIL)
        .nameFormat(Attribute.URI_REFERENCE)
        .value("martin@litsec.se")
        .value("martin.lindstrom@litsec.se")
        .build();
    
    List<String> values = AttributeUtils.getAttributeStringValues(attribute);
    Assert.assertEquals(Arrays.asList("martin@litsec.se", "martin.lindstrom@litsec.se"), values);
    
    // Using the AttributeUtils.getAttributeStringValue method only gives the first value.
    Assert.assertEquals("martin@litsec.se", AttributeUtils.getAttributeStringValue(attribute));
  }

  @Test
  public void testGetAttributeStringValue() {
    
    Attribute attribute = AttributeBuilder.builder(AttributeBuilderTest.ATTRIBUTE_NAME_SN)
        .friendlyName(AttributeBuilderTest.ATTRIBUTE_FRIENDLY_NAME_SN)
        .nameFormat(Attribute.URI_REFERENCE)
        .value("Eriksson")
        .build();

    String value = AttributeUtils.getAttributeStringValue(attribute);
    Assert.assertEquals("Eriksson", value);
    
    // Using the AttributeUtils.getAttributeStringValues method gives a list with one element.
    Assert.assertEquals(Arrays.asList("Eriksson"), AttributeUtils.getAttributeStringValues(attribute));
  }
  
  @Test
  public void testGetAttributeValues() {
    
    Attribute attribute = AttributeBuilder.builder(AttributeBuilderTest.ATTRIBUTE_NAME_MAIL)
        .friendlyName(AttributeBuilderTest.ATTRIBUTE_FRIENDLY_NAME_MAIL)
        .nameFormat(Attribute.URI_REFERENCE)
        .value("martin@litsec.se")
        .value("martin.lindstrom@litsec.se")
        .build();
    
    List<XSString> values = AttributeUtils.getAttributeValues(attribute, XSString.class);
    Assert.assertTrue(values.size() == 2);
    Assert.assertEquals(Arrays.asList("martin@litsec.se", "martin.lindstrom@litsec.se"),
      values.stream().filter(a -> XSString.class.isInstance(a)).map(XSString.class::cast).map(s -> s.getValue()).collect(Collectors.toList()));
    
    XSBoolean value1 = AttributeBuilder.createValueObject(XSBoolean.class);
    value1.setValue(XSBooleanValue.valueOf("true"));
    XSBoolean value2 = AttributeBuilder.createValueObject(XSBoolean.class);
    value2.setValue(XSBooleanValue.valueOf("false"));
    
    attribute = AttributeBuilder.builder("http://eid.litsec.se/types/boolean")
        .friendlyName("booleanAttribute")
        .nameFormat(Attribute.URI_REFERENCE)
        .value(value1)
        .value(value2)
        .build();
    
    List<XSBoolean> bvalues = AttributeUtils.getAttributeValues(attribute, XSBoolean.class);
    Assert.assertTrue(values.size() == 2);
    Assert.assertEquals(Arrays.asList(Boolean.TRUE, Boolean.FALSE),
      bvalues.stream().filter(a -> XSBoolean.class.isInstance(a)).map(XSBoolean.class::cast)
      .map(b -> b.getValue().getValue()).collect(Collectors.toList()));
  }
  
  @Test
  public void testGetAttributeValue() {
    
    Attribute attribute = AttributeBuilder.builder(AttributeBuilderTest.ATTRIBUTE_NAME_SN)
        .friendlyName(AttributeBuilderTest.ATTRIBUTE_FRIENDLY_NAME_SN)
        .nameFormat(Attribute.URI_REFERENCE)
        .value("Eriksson")
        .build();
    
    XSString value = AttributeUtils.getAttributeValue(attribute, XSString.class);
    Assert.assertEquals("Eriksson", value.getValue());
        
    XSBoolean bvalue = AttributeBuilder.createValueObject(XSBoolean.class);
    bvalue.setValue(XSBooleanValue.valueOf("true"));
    
    attribute = AttributeBuilder.builder("http://eid.litsec.se/types/boolean")
        .friendlyName("booleanAttribute")
        .nameFormat(Attribute.URI_REFERENCE)
        .value(bvalue)
        .build();
    
    XSBoolean b = AttributeUtils.getAttributeValue(attribute, XSBoolean.class);
    Assert.assertEquals(Boolean.TRUE, b.getValue().getValue());
  }

  @Test
  public void testGetAttribute() {
    
    List<Attribute> attributes = Arrays.asList(
      AttributeBuilder.builder(AttributeBuilderTest.ATTRIBUTE_NAME_MAIL).value("martin@litsec.se").value("martin.lindstrom@litsec.se").build(),
      AttributeBuilder.builder(AttributeBuilderTest.ATTRIBUTE_NAME_SN).value("Eriksson").build());
    
    Optional<Attribute> attr = AttributeUtils.getAttribute("http://eid.litsec.se/types/boolean", attributes);
    Assert.assertFalse(attr.isPresent());
    
    attr = AttributeUtils.getAttribute(AttributeBuilderTest.ATTRIBUTE_NAME_MAIL, attributes);
    Assert.assertTrue(attr.isPresent());
    Assert.assertEquals(AttributeBuilderTest.ATTRIBUTE_NAME_MAIL, attr.get().getName());
    
    attr = AttributeUtils.getAttribute(AttributeBuilderTest.ATTRIBUTE_NAME_SN, attributes);
    Assert.assertTrue(attr.isPresent());
    Assert.assertEquals(AttributeBuilderTest.ATTRIBUTE_NAME_SN, attr.get().getName());    
  }
  
}
