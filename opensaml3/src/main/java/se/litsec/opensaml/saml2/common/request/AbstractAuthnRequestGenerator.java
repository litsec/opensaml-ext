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
   * @return a {@code SingleSignOnService} object
   * @throws AuthnRequestGeneratorException
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
