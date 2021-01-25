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

import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.saml2.core.AuthnRequest;

import se.litsec.opensaml.common.validation.AbstractValidationParametersBuilder;
import se.litsec.opensaml.common.validation.CoreValidatorParameters;

/**
 * Abstract builder class for building the {@link ValidationContext} object for use as validation input to the
 * {@link ResponseValidator}.
 * 
 * <p>
 * The reason for all fiddling with generics is that we want to be able to subclass the builder classes.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public abstract class AbstractResponseValidationParametersBuilder<T extends AbstractResponseValidationParametersBuilder<T>> extends
    AbstractValidationParametersBuilder<T> {

  /**
   * Assigns the instant (millisecond since epoch) when the response message was received.
   * 
   * @param receiveInstant
   *          timestamp for when the message was received
   * @return the builder
   */
  public T receiveInstant(Long receiveInstant) {
    return this.staticParameter(CoreValidatorParameters.RECEIVE_INSTANT, receiveInstant);
  }

  /**
   * Assigns the URL on which the message was received.
   * 
   * @param receiveUrl
   *          the URL
   * @return the builder
   */
  public T receiveUrl(String receiveUrl) {
    return this.staticParameter(CoreValidatorParameters.RECEIVE_URL, receiveUrl);
  }

  /**
   * Assigns the expected issuer to be used when checking the issuer of an element.
   * 
   * @param expectedIssuer
   *          the issuer entityID
   * @return the builder
   */
  public T expectedIssuer(String expectedIssuer) {
    return this.staticParameter(CoreValidatorParameters.EXPECTED_ISSUER, expectedIssuer);
  }

  /**
   * Assigns the {@code AuthnRequest} that was sent to give the message we are validating.
   * 
   * @param authnRequest
   *          an AuthnRequest message
   * @return the builder
   */
  public T authnRequest(AuthnRequest authnRequest) {
    return this.staticParameter(CoreValidatorParameters.AUTHN_REQUEST, authnRequest);
  }

  /**
   * Assigns the ID of the {@code AuthnRequest} that was sent to give the message we are validating.
   * 
   * @param authnRequestID
   *          ID
   * @return the builder
   */
  public T authnRequestID(String authnRequestID) {
    return this.staticParameter(CoreValidatorParameters.AUTHN_REQUEST_ID, authnRequestID);
  }

}
