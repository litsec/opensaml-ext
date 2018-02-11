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

import org.opensaml.saml.common.SAMLObject;

/**
 * Interface for protecting against SAML message replay attacks.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public interface MessageReplayChecker {

  /**
   * Checks if the supplied message ID already has been processed within the time the replay checker keeps the processed
   * items in its cache.
   * 
   * @param id
   *          the message ID
   * @throws MessageReplayException
   *           if there is a replay attack
   */
  void checkReplay(String id) throws MessageReplayException;

  /**
   * Checks if the supplied message contains an ID that already has been processed within the time the replay checker
   * keeps the processed items in its cache.
   * 
   * @param object
   *          the SAML message object
   * @throws MessageReplayException
   *           if there is a replay attack
   * @throws IllegalArgumentException
   *           if the supplied object is not supported by the cheker
   */
  void checkReplay(SAMLObject object) throws MessageReplayException, IllegalArgumentException;

}
