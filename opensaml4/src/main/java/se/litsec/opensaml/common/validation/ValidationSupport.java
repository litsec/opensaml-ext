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
  public static void check(final ValidationResult result) throws ValidationResultException {
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
    public ValidationResultException(final ValidationResult result) {
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
