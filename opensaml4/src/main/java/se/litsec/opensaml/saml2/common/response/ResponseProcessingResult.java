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
import java.util.List;

import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Attribute;
import org.opensaml.saml.saml2.core.NameID;

/**
 * Interface that describes the result of a response processing operation. It contains the actual {@code Assertion} that
 * really holds all information, but also "easy to access" methods of the elements that are of most interest.
 * <p>
 * Note that only successful responses are represented. Error responses are represented using the
 * {@link ResponseStatusErrorException}.
 * </p>
 *
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public interface ResponseProcessingResult {

  /**
   * Returns the {@code Assertion} from the response.
   * 
   * @return the {@code Assertion}
   */
  Assertion getAssertion();

  /**
   * Returns the attributes that are part of the attribute statement of the assertion.
   * 
   * @return an (unmodifiable) list of attributes
   */
  List<Attribute> getAttributes();

  /**
   * Returns the URI for the {@code AuthnContextClassRef} element that holds the "level of assurance" under which the
   * authentication was made.
   * 
   * @return LoA URI
   */
  String getAuthnContextClassUri();

  /**
   * Returns the authentication instant.
   * 
   * @return the instant at which the user authenticated
   */
  Instant getAuthnInstant();

  /**
   * Returns the entityID of the issuing IdP.
   * 
   * @return entityID for the IdP
   */
  String getIssuer();

  /**
   * Returns the {@code NameID} for the subject.
   * 
   * @return the nameID
   */
  NameID getSubjectNameID();

}
