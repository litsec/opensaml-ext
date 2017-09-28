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

import org.opensaml.security.x509.X509Credential;

/**
 * Base interface for the input to a request generator.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public interface RequestGeneratorInput {

  /**
   * Returns the relay state variable to use.
   * 
   * @return relay state
   */
  String getRelayState();
  
  /**
   * Returns the peer (IdP) entityID.
   * 
   * @return the entityID
   */
  String getPeerEntityID();  

  /**
   * If the caller prefers a specific binding to use, this method should return that. Otherwise the request generator
   * its own default.
   * 
   * @return the preferred binding, or {@code null} if the generator default should apply
   */
  String getPreferredBinding();

  /**
   * A request generator normally has a configured signature credential that is used to sign the request. If, for some
   * reason, other credentials should be used to sign a particular request, this method may be implemented. The default
   * returns {@code null}.
   * 
   * @return signature credential that overrides the installed credentials
   */
  default X509Credential getOverrideSigningCredential() {
    return null;
  }

}
