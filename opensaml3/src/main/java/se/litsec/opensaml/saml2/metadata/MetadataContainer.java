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

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.common.SignableSAMLObject;
import org.opensaml.saml.saml2.common.CacheableSAMLObject;
import org.opensaml.saml.saml2.common.TimeBoundSAMLObject;
import org.opensaml.xmlsec.signature.support.SignatureException;
import org.w3c.dom.Element;

/**
 * An interface that encapsulates an {@code EntityDescriptor} or {@code EntitiesDescriptor} in a container and defines
 * useful method - mainly for publishing the metadata for an entity or a federation.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 *
 * @param <T>
 *          the contained type
 */
public interface MetadataContainer<T extends TimeBoundSAMLObject & SignableSAMLObject & CacheableSAMLObject> {
  
  /**
   * Returns the metadata element that is encapsulated by this object.
   * 
   * @return a descriptor object
   */
  T getDescriptor();

  /**
   * Returns a deep clone of the descriptor element that is encapsulated by this object.
   * 
   * @return an copied descriptor object
   * @throws MarshallingException
   *           for marshalling errors of the object
   * @throws UnmarshallingException
   *           for unmarshalling errors of the object
   */
  T cloneDescriptor() throws MarshallingException, UnmarshallingException;

  /**
   * Predicate that returns {@code true} if the contained descriptor needs to be updated regarding its signature
   * status and validity. The method will also take into account the update interval configured for this instance of the
   * container.
   * 
   * @param signatureRequired
   *          should be set if signatures are required for a entry to be regarded valid
   * @return if the encapsulated descriptor needs to be updated {@code true} is returned, otherwise
   *         {@code false}
   */
  boolean updateRequired(boolean signatureRequired);

  /**
   * Updates the encapsulated descriptor with a newly generated ID, a validity time according to this
   * object's configuration, and then optionally signs the record.
   * 
   * @param sign
   *          flag that should be set if the metadata is to be signed
   * @return a reference to the resulting descriptor object
   * @throws SignatureException
   *           for signature errors
   * @throws MarshallingException
   *           for marshalling errors
   * @see #sign()
   */
  T update(boolean sign) throws SignatureException, MarshallingException;

  /**
   * Signs the encapsulated descriptor using the signature credentials configured for this object.
   * 
   * @return a reference to the resulting descriptor object
   * @throws SignatureException
   *           for signature errors
   * @throws MarshallingException
   *           for marshalling errors
   * @see #update(boolean)
   */
  T sign() throws SignatureException, MarshallingException;

  /**
   * Marshals the encapsulated descriptor into its XML representation.
   * 
   * @return an XML element
   * @throws MarshallingException
   *           for marshalling errors
   */
  Element marshall() throws MarshallingException;

  /**
   * Returns the duration of the validity that the encapsulated descriptor has.
   * 
   * @return the validity time for the metadata
   */
  Duration getValidity();

  /**
   * Returns the factor (between 0 and 1) that is used to compute whether it is time to update the contained
   * descriptor. The higher the factor, the more often the metadata is updated. The "is update required"
   * computation is calculated as follows:
   * 
   * <pre>
   * if (expireInstant > now) {
   *   return <update-required>
   * }
   * else {
   *   return (updateFactor * getValidity()) > (expireInstant - now) ? <update-required> : <no-update-required>
   * }
   * </pre>
   * 
   * The easiest way to get the meaning of the update factor is perhaps using words. Suppose the update factor is 0,5,
   * then the meaning is: "update the metadata when less than 50% of its original validity time remains".
   * 
   * @return the update factor
   */
  float getUpdateFactor();

}
