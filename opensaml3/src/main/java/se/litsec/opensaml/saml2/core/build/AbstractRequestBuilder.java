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
package se.litsec.opensaml.saml2.core.build;

import java.time.ZoneId;

import org.joda.time.DateTime;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SAMLVersion;
import org.opensaml.saml.saml2.core.Extensions;
import org.opensaml.saml.saml2.core.Issuer;
import org.opensaml.saml.saml2.core.NameID;
import org.opensaml.saml.saml2.core.RequestAbstractType;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.utils.ObjectUtils;

/**
 * Abstract builder class for building request messages.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 *
 * @param <T>
 *          the type of request message
 * @param <BUILDER>
 *          the builder type
 */
public abstract class AbstractRequestBuilder<T extends RequestAbstractType, BUILDER extends AbstractSAMLObjectBuilder<T>> extends
    AbstractSAMLObjectBuilder<T> {

  /** {@inheritDoc} */
  @Override
  public T build() {
    if (this.object().getVersion() == null) {
      this.object().setVersion(SAMLVersion.VERSION_20);
    }
    return super.build();
  }

  /**
   * Assigns the version attribute for the request.
   * <p>
   * If not assigned, the {@link SAMLVersion#VERSION_20} will be assigned as a default.
   * </p>
   * 
   * @param major
   *          major version
   * @param minor
   *          minor version
   * @return the builder
   */
  public BUILDER version(int major, int minor) {
    this.object().setVersion(SAMLVersion.valueOf(major, minor));
    return this.getThis();
  }

  /**
   * Assigns the version attribute for the request.
   * <p>
   * If not assigned, the {@link SAMLVersion#VERSION_20} will be assigned as a default.
   * </p>
   * 
   * @param version
   *          the versions
   * @return the builder
   */
  public BUILDER version(String version) {
    this.object().setVersion(SAMLVersion.valueOf(version));
    return this.getThis();
  }

  /**
   * Assigns the {@code ID} attribute of the request.
   * 
   * @param id
   *          the ID
   * @return the builder
   */
  public BUILDER id(String id) {
    this.object().setID(id);
    return this.getThis();
  }

  /**
   * Assigns the issue instant.
   * 
   * @param instant
   *          the issue instant
   * @return the builder
   */
  public BUILDER issueInstant(org.joda.time.DateTime instant) {
    this.object().setIssueInstant(instant);
    return this.getThis();
  }

  /**
   * Assigns the issue instant (using the local time zone).
   * 
   * @param instant
   *          the issue instant
   * @return the builder
   */
  public BUILDER issueInstant(java.time.LocalDateTime instant) {
    if (instant == null) {
      this.object().setIssueInstant(null);
    }
    else {
      this.object().setIssueInstant(new DateTime(instant.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));
    }
    return this.getThis();
  }

  /**
   * Assigns the {@code Destination} attribute of the request.
   * 
   * @param destination
   *          the destination URI
   * @return the builder
   */
  public BUILDER destination(String destination) {
    this.object().setDestination(destination);
    return this.getThis();
  }

  /**
   * Assigns the {@code Consent} attribute of the request.
   * 
   * @param consent
   *          the consent string
   * @return the builder
   */
  public BUILDER consent(String consent) {
    this.object().setConsent(consent);
    return this.getThis();
  }

  /**
   * Assigns the {@code Issuer} element of the request by adding an {@code Issuer} element having the nameID format
   * {@code urn:oasis:names:tc:SAML:2.0:nameid-format:entity}.
   * 
   * @param issuer
   *          the entityID of the issuer
   * @return the builder
   * @see #issuer(Issuer)
   */
  public BUILDER issuer(String issuer) {
    Issuer issuerElement = ObjectUtils.createSamlObject(Issuer.class);
    issuerElement.setValue(issuer);
    issuerElement.setFormat(NameID.ENTITY);
    this.object().setIssuer(issuerElement);
    return this.getThis();
  }

  /**
   * Assigns the {@code Issuer} element of the request.
   * 
   * @param issuer
   *          the issuer (will be cloned before assignment)
   * @return the builder
   */
  public BUILDER issuer(Issuer issuer) {
    try {
      this.object().setIssuer(XMLObjectSupport.cloneXMLObject(issuer));
    }
    catch (MarshallingException | UnmarshallingException e) {
      throw new RuntimeException(e);
    }
    return this.getThis();
  }

  /**
   * Assigns an {@code Extensions} element to the request.
   * 
   * @param extensions
   *          the extensions element to add
   * @return the builder
   */
  public BUILDER extensions(Extensions extensions) {
    this.object().setExtensions(extensions);
    return this.getThis();
  }

  /**
   * In order for us to be able to make chaining calls we need to return the concrete type of the builder.
   * 
   * @return the concrete type of the builder
   */
  protected abstract BUILDER getThis();

}
