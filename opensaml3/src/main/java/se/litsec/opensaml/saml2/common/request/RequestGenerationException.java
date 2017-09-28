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
package se.litsec.opensaml.saml2.common.request;

/**
 * Exception class for indicating errors during a request generation.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class RequestGenerationException extends Exception {

  /** For serializing. */
  private static final long serialVersionUID = -8476694063995230705L;

  /**
   * Constructor taking an error message.
   * 
   * @param message
   *          the error message
   */
  public RequestGenerationException(String message) {
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
  public RequestGenerationException(String message, Throwable cause) {
    super(message, cause);
  }

}
