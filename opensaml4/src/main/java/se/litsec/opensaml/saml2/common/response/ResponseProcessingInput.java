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
package se.litsec.opensaml.saml2.common.response;

import java.time.Instant;

import org.opensaml.saml.saml2.core.AuthnRequest;

/**
 * Represents the input passed along with a SAML Response to the {@link ResponseProcessor}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public interface ResponseProcessingInput {

  /**
   * Returns the authentication request message that corresponds to the response message being processed.
   * 
   * @return the AuthnRequest message or null if no message is available
   */
  AuthnRequest getAuthnRequest();

  /**
   * Returns the RelayState that was included in the request (or {@code null} if none was sent).
   * 
   * @return the RelayState variable or null
   */
  String getRelayState();

  /**
   * Returns the URL on which the response message was received.
   * 
   * @return the receive URL
   */
  String getReceiveURL();

  /**
   * Returns the timestamp when the response was received.
   * 
   * @return the receive timestamp
   */
  Instant getReceiveInstant();

  /**
   * If the validation should perform a check of the Address(es) found in the assertion, this method should return the
   * address of the client, otherwise return {@code null}.
   * 
   * @return the client IP address of null if no check should be made
   */
  String getClientIpAddress();

}
