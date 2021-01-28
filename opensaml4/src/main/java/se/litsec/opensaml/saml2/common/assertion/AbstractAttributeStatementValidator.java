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
package se.litsec.opensaml.saml2.common.assertion;

import java.util.Collections;
import java.util.List;

import javax.xml.namespace.QName;

import org.opensaml.saml.common.assertion.AssertionValidationException;
import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.common.assertion.ValidationResult;
import org.opensaml.saml.saml2.assertion.StatementValidator;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.AttributeStatement;
import org.opensaml.saml.saml2.core.Statement;

/**
 * Abstract validator for {@link AttributeStatement}s.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public abstract class AbstractAttributeStatementValidator implements StatementValidator {

  /** {@inheritDoc} */
  @Override
  public QName getServicedStatement() {
    return AttributeStatement.DEFAULT_ELEMENT_NAME;
  }

  /**
   * Validates that all required attributes were received in the {@code AttributeStatement}.
   */
  @Override
  public ValidationResult validate(final Statement statement, final Assertion assertion, final ValidationContext context)
      throws AssertionValidationException {

    if (statement instanceof AttributeStatement) {
      AttributeStatement attributeStatement = (AttributeStatement) statement;
      List<Attribute> list = attributeStatement.getAttributes() != null ? attributeStatement.getAttributes() : Collections.emptyList();
      return this.validateRequiredAttributes(list, attributeStatement, assertion, context);
    }
    else {
      throw new AssertionValidationException("Illegal call - statement is of type " + statement.getClass().getSimpleName());
    }
  }

  /**
   * Validates that the attribute statement contains all attributes that we require.
   * 
   * @param attributes
   *          the attributes
   * @param statement
   *          the attribute statement
   * @param assertion
   *          the assertion
   * @param context
   *          the validation context
   * @return validation result
   */
  protected abstract ValidationResult validateRequiredAttributes(final List<Attribute> attributes, 
      final AttributeStatement statement, final Assertion assertion, final ValidationContext context);

}
