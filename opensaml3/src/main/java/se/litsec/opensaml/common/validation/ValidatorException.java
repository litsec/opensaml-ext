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
package se.litsec.opensaml.common.validation;

import org.opensaml.saml.common.assertion.ValidationContext;

/**
 * Generic exception class for validator errors.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ValidatorException extends Exception {

  /** For serializing. */
  private static final long serialVersionUID = 1916858576951552731L;

  /**
   * Constructor that initializes based on the supplied {@link ValidationContext}.
   * 
   * @param context
   *          validation context
   */
  public ValidatorException(ValidationContext context) {
    super(context.getValidationFailureMessage());
  }

  /**
   * Constructor accepting an error message.
   * 
   * @param message
   *          the error message
   */
  public ValidatorException(String message) {
    super(message);
  }

  /**
   * Constructor accepting an error message and the cause of the error
   * 
   * @param message
   *          the error message
   * @param cause
   *          the cause of the error
   */
  public ValidatorException(String message, Throwable cause) {
    super(message, cause);
  }

}
