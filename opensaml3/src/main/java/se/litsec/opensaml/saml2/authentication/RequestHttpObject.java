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
package se.litsec.opensaml.saml2.authentication;

import java.util.Map;

import org.opensaml.saml.saml2.core.RequestAbstractType;

/**
 * Defines an interface that represents an object that holds data necessary for the SP application to transmit
 * a request message to an IdP.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public interface RequestHttpObject<T extends RequestAbstractType> {

  /**
   * Returns the complete URL that the SP application should use when the user agent is sent to the Identity Provider.
   * <p>
   * For a redirect, this URL could look something like:
   * 
   * <pre>
   * https://www.theidp.com/auth?SAMLRequest=<encoded request>&RelayState=abcd
   * </pre>
   * 
   * </p>
   * <b>Note:</b> Additional query parameters may be added to the URL by the using system.
   * 
   * @return the URL to use when sending the user to the Identity Provider
   */
  String getSendUrl();

  /**
   * Returns the HTTP method that should be used to send the request, via the user agent, to the Identity Provider.
   * Possible values for this implementation is "GET" (redirect) and "POST".
   * 
   * @return the HTTP method to use
   */
  String getMethod();

  /**
   * If the {@link #getMethod()} returns "POST" the request should be posted to the Identity Provider. The request
   * parameters are represented using a Map where the entries represent parameter names and values.
   * <p>
   * Note: for the "GET" method this method returns {@code null}.
   * </p>
   * 
   * @return a Map holding the POST body
   */
  Map<String, String> getRequestParameters();

  /**
   * Returns a mapping of header names and values that should be used when sending the request.
   * 
   * @return HTTP headers
   */
  Map<String, String> getHttpHeaders();
  
  /**
   * Returns the actual request (for easy access to its elements).
   * 
   * @return the request
   */
  T getRequest();
  
}
