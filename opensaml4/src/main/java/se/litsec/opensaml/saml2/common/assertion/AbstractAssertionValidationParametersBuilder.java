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
package se.litsec.opensaml.saml2.common.assertion;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

import org.opensaml.saml.common.assertion.ValidationContext;
import org.opensaml.saml.saml2.assertion.SAML2AssertionValidationParameters;

import se.litsec.opensaml.saml2.common.response.AbstractResponseValidationParametersBuilder;

/**
 * Abstract builder class for building the {@link ValidationContext} object for use as validation input to the
 * {@link AssertionValidator}.
 * 
 * <p>
 * The reason for all fiddling with generics is that we want to be able to subclass the builder classes.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public abstract class AbstractAssertionValidationParametersBuilder<T extends AbstractAssertionValidationParametersBuilder<T>> extends
    AbstractResponseValidationParametersBuilder<T> {

  /**
   * Assigns the issue instant from the Response message that contained the assertion being validated.
   * 
   * @param instant
   *          the response issue instant
   * @return the builder
   */
  public T responseIssueInstant(Long instant) {
    return this.staticParameter(AssertionValidator.RESPONSE_ISSUE_INSTANT, instant);
  }

  /**
   * Assigns the valid URLs for the intended recipients.
   * 
   * @param recipients
   *          one or more URLs
   * @return the builder
   */
  public T validRecipients(String... recipients) {
    if (recipients != null) {
      Set<String> set = new HashSet<>();
      for (String r : recipients) {
        set.add(r);
      }
      return this.staticParameter(SAML2AssertionValidationParameters.SC_VALID_RECIPIENTS, set);
    }
    else {
      return this.getThis();
    }
  }

  /**
   * Assigns the valid addresses that we allow the user agent to have.
   * 
   * @param addresses
   *          one or more IP addresses
   * @return the builder
   */
  public T validAddresses(InetAddress... addresses) {
    if (addresses != null) {
      Set<InetAddress> set = new HashSet<>();
      for (InetAddress a : addresses) {
        set.add(a);
      }
      return this.staticParameter(SAML2AssertionValidationParameters.SC_VALID_ADDRESSES, set);
    }
    else {
      return this.getThis();
    }
  }

  /**
   * Assigns the entityID:s of the valid audiences.
   * 
   * @param audiences
   *          the audiences
   * @return the builder
   */
  public T validAudiences(String... audiences) {
    if (audiences != null) {
      Set<String> set = new HashSet<>();
      for (String a : audiences) {
        set.add(a);
      }
      return this.staticParameter(SAML2AssertionValidationParameters.COND_VALID_AUDIENCES, set);
    }
    else {
      return this.getThis();
    }
  }

  /**
   * Assigns the {@code ForceAuthn} flag from the corresponding {@code AuthnRequest}.
   * 
   * @param forceAuthn
   *          true/false
   * @return the builder
   */
  public T authnRequestForceAuthn(Boolean forceAuthn) {
    return this.staticParameter(AuthnStatementValidator.AUTHN_REQUEST_FORCE_AUTHN, forceAuthn);
  }

  /**
   * Assigns the issuance time for the corresponding {@code AuthnRequest} when validating an assertion.
   * 
   * @param issueInstant
   *          issue time (in milliseconds since epoch)
   * @return the builder
   */
  public T authnRequestIssueInstant(Long issueInstant) {
    return this.staticParameter(AuthnStatementValidator.AUTHN_REQUEST_ISSUE_INSTANT, issueInstant);
  }

  /**
   * Assigns the maximum session time that we, as a SP, can accept when receiving assertions based on older
   * authentications (SSO).
   * 
   * @param duration
   *          milliseconds
   * @return the builder
   */
  public T maxAcceptedSsoSessionTime(Long duration) {
    return this.staticParameter(AuthnStatementValidator.MAX_ACCEPTED_SSO_SESSION_TIME, duration);
  }

}
