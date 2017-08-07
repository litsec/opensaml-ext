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
package se.litsec.opensaml.core;

/**
 * Runtime exception class for errors when using builders.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SAMLObjectBuilderRuntimeException extends RuntimeException {

  /** For serializing. */
  private static final long serialVersionUID = -2665398281671943709L;

  /**
   * Constructor assigning the error message.
   * 
   * @param message
   *          the error message
   */
  public SAMLObjectBuilderRuntimeException(String message) {
    super(message);
  }

  /**
   * Constructor assigning the cause of the error
   * 
   * @param cause
   *          the cause of the error
   */
  public SAMLObjectBuilderRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructor assinging the error message and the cause of the error.
   * 
   * @param message
   *          the error message
   * @param cause
   *          the cause of the error
   */
  public SAMLObjectBuilderRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }

}
