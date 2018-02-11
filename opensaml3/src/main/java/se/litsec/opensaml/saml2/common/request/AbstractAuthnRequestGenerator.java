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

import java.util.function.Predicate;

import org.opensaml.saml.common.xml.SAMLConstants;
import org.opensaml.saml.saml2.core.AuthnRequest;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for generating AuthnRequest messages.
 * 
 * @param <I>
 *          the type of the input required by this generator
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public abstract class AbstractAuthnRequestGenerator<I extends RequestGeneratorInput> extends AbstractRequestGenerator<AuthnRequest, I>
    implements AuthnRequestGenerator<I> {

  /** Logging instance. */
  private final Logger log = LoggerFactory.getLogger(AbstractAuthnRequestGenerator.class);

  /** Function for checking if a binding is valid. */
  protected static Predicate<String> isValidBinding = b -> SAMLConstants.SAML2_POST_BINDING_URI.equals(b)
      || SAMLConstants.SAML2_REDIRECT_BINDING_URI.equals(b);

  /** Function for checking if a metadata SingleSignOnService element has a binding that we support. */
  protected static Predicate<SingleSignOnService> hasSupportedBinding = s -> isValidBinding.test(s.getBinding());

  /**
   * Constructor.
   * 
   * @param entityID
   *          the entityID
   */
  public AbstractAuthnRequestGenerator(String entityID) {
    super(entityID);
  }

  /**
   * Returns the {@code SingleSignOnService} element to use when sending the request to the IdP. The preferred binding
   * will be searched for first, and if this is not found, another binding the we support will be used.
   * 
   * @param idp
   *          the IdP metadata
   * @param input
   *          input for generating
   * @return a {@code SingleSignOnService} object
   * @throws RequestGenerationException
   *           if not valid endpoint can be found
   */
  protected SingleSignOnService getSingleSignOnService(EntityDescriptor idp, RequestGeneratorInput input)
      throws RequestGenerationException {

    String preferBinding = input.getPreferredBinding() != null ? input.getPreferredBinding() : this.getDefaultBinding();

    IDPSSODescriptor descriptor = idp.getIDPSSODescriptor(SAMLConstants.SAML20P_NS);
    if (descriptor == null) {
      throw new RequestGenerationException("Invalid IdP metadata - missing IDPSSODescriptor");
    }
    SingleSignOnService ssoService = descriptor.getSingleSignOnServices()
      .stream()
      .filter(s -> preferBinding.equals(s.getBinding()))
      .findFirst()
      .orElse(null);
    if (ssoService == null) {
      ssoService = descriptor.getSingleSignOnServices().stream().filter(hasSupportedBinding).findFirst().orElse(null);
    }
    if (ssoService == null) {
      String msg = String.format("IdP '%s' does not specify endpoints for POST or Redirect - cannot send request", idp.getEntityID());
      log.error(msg);
      throw new RequestGenerationException(msg);
    }
    return ssoService;
  }

}
