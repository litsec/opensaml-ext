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
package se.litsec.opensaml.saml2.metadata;

import java.time.Duration;

import org.joda.time.DateTime;
import org.joda.time.chrono.ISOChronology;
import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.saml2.common.CacheableSAMLObject;
import org.opensaml.saml.saml2.common.TimeBoundSAMLObject;
import org.opensaml.security.x509.X509Credential;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import net.shibboleth.utilities.java.support.security.RandomIdentifierGenerationStrategy;
import se.litsec.opensaml.utils.ObjectUtils;
import se.litsec.opensaml.utils.SignatureUtils;

/**
 * Abstract base class for the {@link MetadataContainer} interface.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 *
 * @param <T>
 *          the contained type
 */
public abstract class AbstractMetadataContainer<T extends TimeBoundSAMLObject & SignableSAMLObject & CacheableSAMLObject> implements
    MetadataContainer<T> {

  /** The default validity for metadata - one week. */
  public static final Duration DEFAULT_VALIDITY = Duration.ofDays(7);

  /**
   * The default update factor for the metadata - 0,75 (75%), i.e.
   * "update the metadata when less than 75% of its original validity time remains".
   * 
   * @see #getUpdateFactor()
   */
  public static final float DEFAULT_UPDATE_FACTOR = 0.75f;

  /** Default size for the ID attribute string. */
  public static final int DEFAULT_DESCRIPTOR_ID_SIZE = 32;

  /** Logging instance. */
  private Logger logger = LoggerFactory.getLogger(AbstractMetadataContainer.class);

  /** The encapsulated descriptor element. */
  protected T descriptor;

  /** The validity time for created entries. */
  protected Duration validity = DEFAULT_VALIDITY;

  /** The update factor. */
  protected float updateFactor = DEFAULT_UPDATE_FACTOR;

  /** The size of the ID attribute string. */
  protected int idSize = DEFAULT_DESCRIPTOR_ID_SIZE;

  /** The signature credentials for signing the metadata entry. */
  protected X509Credential signatureCredentials;

  /**
   * Constructor assigning the encapsulated descriptor element.
   * 
   * @param descriptor
   *          the descriptor object
   * @param signatureCredentials
   *          the signature credentials for signing the descriptor. May be {@code null}, but then no signing will be
   *          possible
   */
  public AbstractMetadataContainer(T descriptor, X509Credential signatureCredentials) {
    this.descriptor = descriptor;
    this.signatureCredentials = signatureCredentials;
  }

  /** {@inheritDoc} */
  @Override
  public T getDescriptor() {
    return this.descriptor;
  }

  /** {@inheritDoc} */
  @Override
  public T cloneDescriptor() throws MarshallingException, UnmarshallingException {
    return XMLObjectSupport.cloneXMLObject(this.descriptor);
  }

  /** {@inheritDoc} */
  @Override
  public boolean updateRequired(boolean signatureRequired) {
    if (!this.descriptor.isValid() || (signatureRequired && !this.descriptor.isSigned())) {
      return true;
    }
    if (this.descriptor.getValidUntil() == null) {
      return true;
    }
    long expireInstant = this.descriptor.getValidUntil().getMillis();
    long now = new DateTime(ISOChronology.getInstanceUTC()).getMillis();

    return (this.updateFactor * this.validity.toMillis()) > (expireInstant - now);
  }

  /** {@inheritDoc} */
  @Override
  public synchronized T update(boolean sign) throws SignatureException, MarshallingException {

    // Reset the signature
    this.descriptor.setSignature(null);
    
    // Generate a new ID.
    RandomIdentifierGenerationStrategy generator = new RandomIdentifierGenerationStrategy(this.idSize);
    this.assignID(this.descriptor, generator.generateIdentifier(true));

    // Assign the validity.
    DateTime now = new DateTime(ISOChronology.getInstanceUTC());
    DateTime validUntil = now.plusSeconds((int) this.validity.getSeconds());
    this.descriptor.setValidUntil(validUntil);

    logger.debug("Descriptor '{}' was updated with ID '{}' and validUntil '{}'",
      this.getLogString(this.descriptor), this.getID(this.descriptor), this.descriptor.getValidUntil().toString());

    return sign ? this.sign() : this.descriptor;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized T sign() throws SignatureException, MarshallingException {

    logger.trace("Signing descriptor '{}' ...", this.getLogString(this.descriptor));

    if (this.getID(this.descriptor) == null || this.descriptor.getValidUntil() == null) {
      return this.update(true);
    }
    
    SignatureUtils.sign(this.descriptor, this.signatureCredentials);
    logger.debug("Descriptor '{}' successfully signed.", this.getLogString(this.descriptor));

    return this.descriptor;
  }

  /** {@inheritDoc} */
  @Override
  public synchronized Element marshall() throws MarshallingException {
    return ObjectUtils.marshall(this.descriptor);
  }

  /** {@inheritDoc} */
  @Override
  public Duration getValidity() {
    return this.validity;
  }

  /**
   * Assigns the duration of the validity that the encapsulated {@code EntityDescriptor} should have.
   * <p>
   * The default value is {@link #DEFAULT_VALIDITY}.
   * </p>
   * 
   * @param validity
   *          the validity
   */
  public void setValidity(Duration validity) {
    this.validity = validity;
  }

  /** {@inheritDoc} */
  @Override
  public float getUpdateFactor() {
    return this.updateFactor;
  }

  /**
   * Assigns the factor (between 0 and 1) that is used to compute whether it is time to update the contained descriptor.
   * <p>
   * The default value is {@link #DEFAULT_UPDATE_FACTOR}.
   * </p>
   * 
   * @param updateFactor
   *          the update factor
   * @see #getUpdateFactor()
   */
  public void setUpdateFactor(float updateFactor) {
    if (updateFactor < 0 || updateFactor > 1) {
      throw new IllegalArgumentException("Supplied updateFactor must be greater than 0 and equal or less than 1");
    }
    this.updateFactor = updateFactor;
  }

  /**
   * Returns the size of the ID attribute that is generated.
   * 
   * @return the size
   */
  public int getIdSize() {
    return this.idSize;
  }

  /**
   * Assigns the size of the ID attribute that is generated.
   *
   * <p>
   * The default value is {@link #DEFAULT_DESCRIPTOR_ID_SIZE}.
   * </p>
   * 
   * @param idSize
   *          the size
   */
  public void setIdSize(int idSize) {
    this.idSize = idSize;
  }

  /**
   * Returns the ID attribute of the supplied descriptor.
   * 
   * @param descriptor
   *          the descriptor
   * @return the ID attribute
   */
  protected abstract String getID(T descriptor);

  /**
   * Assigns the supplied id to the ID attribute of the descriptor.
   * 
   * @param descriptor
   *          the descriptor
   * @param id
   *          the ID attribute value
   */
  protected abstract void assignID(T descriptor, String id);

  /**
   * Returns a log string of the supplied descriptor.
   * 
   * @param descriptor
   *          the descriptor
   * @return the log string
   */
  protected abstract String getLogString(T descriptor);

}
