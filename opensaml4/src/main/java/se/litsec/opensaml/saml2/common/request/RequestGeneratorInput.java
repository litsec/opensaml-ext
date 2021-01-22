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
