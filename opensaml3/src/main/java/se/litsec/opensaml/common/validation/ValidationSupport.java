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

import org.opensaml.saml.common.assertion.ValidationResult;

/**
 * Support methods and functions for validator implementations.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ValidationSupport {

  // Hidden
  private ValidationSupport() {
  }

  /**
   * Checks if the result is VALID. If not a {@code ValidationResultException} is thrown.
   * 
   * @param result
   *          the result to check
   * @throws ValidationResultException
   *           for non VALID results
   */
  public static void check(ValidationResult result) throws ValidationResultException {
    if (!ValidationResult.VALID.equals(result)) {
      throw new ValidationResultException(result);
    }
  }

  /**
   * Exception class that should be used internally by validators to process errors.
   */
  public static class ValidationResultException extends Exception {

    /** For serializing. */
    private static final long serialVersionUID = -39188491121249365L;

    /** The validation result. */
    private ValidationResult result;

    /**
     * Constructor.
     * 
     * @param result
     *          the validation result - must not be {@link ValidationResult#VALID}
     */
    public ValidationResultException(ValidationResult result) {
      if (ValidationResult.VALID.equals(result)) {
        throw new IllegalArgumentException("Result is valid - can not throw ValidationResultException");
      }
      if (result == null) {
        throw new IllegalArgumentException("Result is null - can not throw ValidationResultException");
      }
      this.result = result;
    }

    /**
     * Returns the validation result.
     * 
     * @return the validation result
     */
    public ValidationResult getResult() {
      return this.result;
    }

  }

}
