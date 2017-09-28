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
 * Abstract base class for request generator input.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public abstract class AbstractRequestGeneratorInput implements RequestGeneratorInput {

  /** Peer entityID. */
  private String peerEntityID;

  /** The RelayState for the request. */
  private String relayState;

  /** The preferred binding. */
  private String preferredBinding;

  /** {@inheritDoc} */
  @Override
  public String getRelayState() {
    return this.relayState;
  }

  /**
   * Assigns the relay state for the request.
   * 
   * @param relayState
   *          the relay state, or {@code null}
   */
  public void setRelayState(String relayState) {
    this.relayState = relayState;
  }

  /** {@inheritDoc} */
  @Override
  public String getPeerEntityID() {
    return this.peerEntityID;
  }

  /**
   * Assigns the peer (IdP) entityID.
   * 
   * @param peerEntityID
   *          the entityID
   */
  public void setPeerEntityID(String peerEntityID) {
    this.peerEntityID = peerEntityID;
  }
  
  /** {@inheritDoc} */
  @Override
  public String getPreferredBinding() {
    return this.preferredBinding;
  }

  /**
   * Assigns the preferred binding to use for the request.
   * 
   * @param preferredBinding
   *          binding, or {@code null}
   */
  public void setPreferredBinding(String preferredBinding) {
    this.preferredBinding = preferredBinding;
  }

}
