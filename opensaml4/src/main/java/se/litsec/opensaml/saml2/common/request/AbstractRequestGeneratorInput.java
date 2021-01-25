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
