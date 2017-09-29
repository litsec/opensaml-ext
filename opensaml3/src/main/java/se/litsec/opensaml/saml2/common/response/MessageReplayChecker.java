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
