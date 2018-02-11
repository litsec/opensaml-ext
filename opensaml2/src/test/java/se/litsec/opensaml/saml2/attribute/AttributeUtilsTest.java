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
package se.litsec.opensaml.saml2.attribute;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.xml.schema.XSBoolean;
import org.opensaml.xml.schema.XSBooleanValue;
import org.opensaml.xml.schema.XSString;

import se.litsec.opensaml.OpenSAMLTestBase;

/**
 * Test cases for {@link AttributeUtils}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AttributeUtilsTest extends OpenSAMLTestBase {
  
  @Test
  public void testGetAttributeStringValues() {
    
    Attribute attribute = AttributeBuilder.BUILDER(AttributeBuilderTest.ATTRIBUTE_NAME_MAIL)
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
    
    Attribute attribute = AttributeBuilder.BUILDER(AttributeBuilderTest.ATTRIBUTE_NAME_SN)
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
    
    Attribute attribute = AttributeBuilder.BUILDER(AttributeBuilderTest.ATTRIBUTE_NAME_MAIL)
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
    
    attribute = AttributeBuilder.BUILDER("http://eid.litsec.se/types/boolean")
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
    
    Attribute attribute = AttributeBuilder.BUILDER(AttributeBuilderTest.ATTRIBUTE_NAME_SN)
        .friendlyName(AttributeBuilderTest.ATTRIBUTE_FRIENDLY_NAME_SN)
        .nameFormat(Attribute.URI_REFERENCE)
        .value("Eriksson")
        .build();
    
    XSString value = AttributeUtils.getAttributeValue(attribute, XSString.class);
    Assert.assertEquals("Eriksson", value.getValue());
        
    XSBoolean bvalue = AttributeBuilder.createValueObject(XSBoolean.class);
    bvalue.setValue(XSBooleanValue.valueOf("true"));
    
    attribute = AttributeBuilder.BUILDER("http://eid.litsec.se/types/boolean")
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
      AttributeBuilder.BUILDER(AttributeBuilderTest.ATTRIBUTE_NAME_MAIL).value("martin@litsec.se").value("martin.lindstrom@litsec.se").build(),
      AttributeBuilder.BUILDER(AttributeBuilderTest.ATTRIBUTE_NAME_SN).value("Eriksson").build());
    
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
