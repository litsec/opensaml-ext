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
package se.litsec.opensaml.saml2.attribute;

import java.io.ByteArrayInputStream;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.opensaml.core.xml.schema.XSBoolean;
import org.opensaml.core.xml.schema.XSBooleanValue;
import org.opensaml.core.xml.schema.XSDateTime;
import org.opensaml.core.xml.schema.XSInteger;
import org.opensaml.core.xml.schema.XSString;
import org.opensaml.saml.saml2.core.Attribute;

import se.litsec.opensaml.OpenSAMLTestBase;
import se.litsec.opensaml.utils.ObjectUtils;

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
  public void testGetAttributeStringValueNoType() throws Exception {
    String xml = "<saml2:Attribute FriendlyName=\"sn\" Name=\"urn:oid:2.5.4.4\" " +
        "NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\" xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\">" +
        "<saml2:AttributeValue>Eriksson</saml2:AttributeValue>" +
        "</saml2:Attribute>";

    Attribute attribute = ObjectUtils.unmarshall(new ByteArrayInputStream(xml.getBytes()), Attribute.class);

    String value = AttributeUtils.getAttributeStringValue(attribute);
    Assert.assertEquals("Eriksson", value);

    // Using the AttributeUtils.getAttributeStringValues method gives a list with one element.
    Assert.assertEquals(Arrays.asList("Eriksson"), AttributeUtils.getAttributeStringValues(attribute));
  }

  @Test
  public void testGetAttributeValueBoolean() throws Exception {
    XSBoolean bool = AttributeBuilder.createValueObject(XSBoolean.class);
    bool.setValue(XSBooleanValue.valueOf("true"));

    Attribute attribute = AttributeBuilder.builder("http://id.litsec.se/attr/bool")
      .friendlyName("LitsecBool")
      .nameFormat(Attribute.URI_REFERENCE)
      .value(bool)
      .build();
    
    XSBoolean value = AttributeUtils.getAttributeValue(attribute, XSBoolean.class);
    Assert.assertNotNull(value);
    Assert.assertTrue(value.getValue().getValue());

    // Try the same, but with no xsi:type declaration
    //
    String xml = "<saml2:Attribute FriendlyName=\"LitsecBool\" Name=\"http://id.litsec.se/attr/bool\" " +
        "NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\" xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"> " +
        "<saml2:AttributeValue>true</saml2:AttributeValue> " +
        "</saml2:Attribute>";

    Attribute attribute2 = ObjectUtils.unmarshall(new ByteArrayInputStream(xml.getBytes()), Attribute.class);
    XSBoolean value2 = AttributeUtils.getAttributeValue(attribute2, XSBoolean.class);
    Assert.assertNotNull(value2);
    Assert.assertTrue(value2.getValue().getValue());
    
    // Should not work
    Assert.assertNull(AttributeUtils.getAttributeValue(attribute2, XSInteger.class));
  }
  
  @Test
  public void testGetAttributeValueInteger() throws Exception {
    XSInteger integer = AttributeBuilder.createValueObject(XSInteger.class);
    integer.setValue(42);

    Attribute attribute = AttributeBuilder.builder("http://id.litsec.se/attr/int")
      .friendlyName("Litsecint")
      .nameFormat(Attribute.URI_REFERENCE)
      .value(integer)
      .build();
    
    XSInteger value = AttributeUtils.getAttributeValue(attribute, XSInteger.class);
    Assert.assertNotNull(value);
    Assert.assertEquals(42, value.getValue().intValue());

    // Try the same, but with no xsi:type declaration
    //
    String xml = "<saml2:Attribute FriendlyName=\"Litsecint\"  Name=\"http://id.litsec.se/attr/int\" " +
        "NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\" xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"> " +
        "<saml2:AttributeValue>42</saml2:AttributeValue> </saml2:Attribute>";

    Attribute attribute2 = ObjectUtils.unmarshall(new ByteArrayInputStream(xml.getBytes()), Attribute.class);
    XSInteger value2 = AttributeUtils.getAttributeValue(attribute2, XSInteger.class);
    Assert.assertNotNull(value2);
    Assert.assertEquals(42, value2.getValue().intValue());
    
    // Should not work
    Assert.assertNull(AttributeUtils.getAttributeValue(attribute2, XSBoolean.class));
  }
  
  @Test
  public void testGetAttributeValueDateTime() throws Exception {
    
    Instant date = Instant.parse("2018-12-10T15:10:21Z");
    XSDateTime xmlDate = AttributeBuilder.createValueObject(XSDateTime.class);
    xmlDate.setValue(date);
    
    Attribute attribute = AttributeBuilder.builder("http://id.litsec.se/attr/date")
      .friendlyName("LitsecDate")
      .nameFormat(Attribute.URI_REFERENCE)
      .value(xmlDate)
      .build();
    
    XSDateTime value = AttributeUtils.getAttributeValue(attribute, XSDateTime.class);
    Assert.assertNotNull(value);
    Assert.assertEquals(date, value.getValue());
    
    // Try the same, but with no xsi:type declaration
    //
    String xml = "<saml2:Attribute FriendlyName=\"LitsecDate\" Name=\"http://id.litsec.se/attr/date\" " +
        "NameFormat=\"urn:oasis:names:tc:SAML:2.0:attrname-format:uri\" xmlns:saml2=\"urn:oasis:names:tc:SAML:2.0:assertion\"> " +
        "<saml2:AttributeValue>2018-12-10T15:10:21.000Z</saml2:AttributeValue> </saml2:Attribute>";
    
    Attribute attribute2 = ObjectUtils.unmarshall(new ByteArrayInputStream(xml.getBytes()), Attribute.class);
    XSDateTime value2 = AttributeUtils.getAttributeValue(attribute2, XSDateTime.class);
    Assert.assertNotNull(value2);
    Assert.assertEquals(date, value2.getValue());
    
    // Should not work
    Assert.assertNull(AttributeUtils.getAttributeValue(attribute2, XSBoolean.class));
    Assert.assertNull(AttributeUtils.getAttributeValue(attribute2, XSInteger.class));
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
      values.stream()
        .filter(a -> XSString.class.isInstance(a))
        .map(XSString.class::cast)
        .map(s -> s.getValue())
        .collect(Collectors.toList()));

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
      bvalues.stream()
        .filter(a -> XSBoolean.class.isInstance(a))
        .map(XSBoolean.class::cast)
        .map(b -> b.getValue().getValue())
        .collect(Collectors.toList()));
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
      AttributeBuilder.builder(AttributeBuilderTest.ATTRIBUTE_NAME_MAIL)
        .value("martin@litsec.se")
        .value("martin.lindstrom@litsec.se")
        .build(),
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
