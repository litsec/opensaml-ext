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
package se.litsec.opensaml.saml2.common.response;

/**
 * Exception class for response validation errors.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ResponseValidationException extends ResponseProcessingException {

  /** For serializing. */
  private static final long serialVersionUID = -5922797771498993278L;

  /**
   * Constructor taking an error message.
   * 
   * @param message
   *          the error message
   */  
  public ResponseValidationException(String message) {
    super(message);
  }

  /**
   * Constructor taking an error message and the cause of the error.
   * 
   * @param message
   *          the error message
   * @param cause
   *          the cause of the error
   */  
  public ResponseValidationException(String message, Throwable cause) {
    super(message, cause);
  }

}
