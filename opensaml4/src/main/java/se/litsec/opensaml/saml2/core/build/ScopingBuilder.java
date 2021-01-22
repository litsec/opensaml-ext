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
package se.litsec.opensaml.saml2.core.build;

import java.util.Arrays;
import java.util.List;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.opensaml.saml.saml2.core.GetComplete;
import org.opensaml.saml.saml2.core.IDPEntry;
import org.opensaml.saml.saml2.core.IDPList;
import org.opensaml.saml.saml2.core.RequesterID;
import org.opensaml.saml.saml2.core.Scoping;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.SAMLObjectBuilderRuntimeException;

/**
 * Builder class for {@code Scoping} elements.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ScopingBuilder extends AbstractSAMLObjectBuilder<Scoping> {

  /**
   * Utility method that creates a builder.
   * 
   * @return a builder
   */
  public static ScopingBuilder builder() {
    return new ScopingBuilder();
  }

  /**
   * Assigns the {@code ProxyCount} attribute.
   * 
   * @param count
   *          the proxy count
   * @return the builder
   */
  public ScopingBuilder proxyCount(Integer count) {
    this.object().setProxyCount(count);
    return this;
  }

  /**
   * Adds the {@code IDPList} element.
   * 
   * @param completeUri
   *          the {@code GetComplete} element of the {@code IDPList} element (may be {@code null})
   * @param idpEntries
   *          the {@code IDPEntry} elements of the {@code IDPList} element
   * @return the builder
   */
  public ScopingBuilder idpList(String completeUri, List<IDPEntry> idpEntries) {
    if (completeUri == null && (idpEntries == null || idpEntries.isEmpty())) {
      this.object().setIDPList(null);
    }
    else {
      IDPList idpList = (IDPList) XMLObjectSupport.buildXMLObject(IDPList.DEFAULT_ELEMENT_NAME);
      if (completeUri != null) {
        GetComplete getComplete = (GetComplete) XMLObjectSupport.buildXMLObject(GetComplete.DEFAULT_ELEMENT_NAME);
        getComplete.setURI(completeUri);
        idpList.setGetComplete(getComplete);
      }
      if (idpEntries != null) {
        for (IDPEntry e : idpEntries) {
          try {
            idpList.getIDPEntrys().add(XMLObjectSupport.cloneXMLObject(e));
          }
          catch (MarshallingException | UnmarshallingException e1) {
            throw new SAMLObjectBuilderRuntimeException(e1);
          }
        }
      }
      this.object().setIDPList(idpList);
    }
    return this;
  }

  /**
   * @see #idpList(String, List)
   * 
   * @param completeUri
   *          the {@code GetComplete} element of the {@code IDPList} element (may be {@code null})
   * @param idpEntries
   *          the {@code IDPEntry} elements of the {@code IDPList} element
   * @return the builder
   */
  public ScopingBuilder idpList(String completeUri, IDPEntry... idpEntries) {
    return this.idpList(completeUri, idpEntries != null ? Arrays.asList(idpEntries) : null);
  }

  /**
   * Creates an {@code IDPEntry} element.
   * 
   * @param providerID
   *          the {@code ProviderID} attribute
   * @param name
   *          the {@code Name} attribute
   * @param loc
   *          the {@code Loc} attribute
   * @return an {@code IDPEntry} element
   */
  public static IDPEntry idpEntry(String providerID, String name, String loc) {
    IDPEntry entry = (IDPEntry) XMLObjectSupport.buildXMLObject(IDPEntry.DEFAULT_ELEMENT_NAME);
    entry.setProviderID(providerID);
    entry.setName(name);
    entry.setLoc(loc);
    return entry;
  }

  /**
   * Assigns {@code RequesterID} elements.
   * 
   * @param ids
   *          the {@code RequesterID} elements to add
   * @return the builder
   */
  public ScopingBuilder requesterIDs(List<String> ids) {
    if (ids == null || ids.isEmpty()) {
      this.object().getRequesterIDs().clear();
    }
    else {
      for (String id : ids) {
        RequesterID ri = (RequesterID) XMLObjectSupport.buildXMLObject(RequesterID.DEFAULT_ELEMENT_NAME); 
        ri.setURI(id);
        this.object().getRequesterIDs().add(ri);
      }
    }
    return this;
  }

  /**
   * @see #requesterIDs(List)
   * 
   * @param ids
   *          the {@code RequesterID} elements to add
   * @return the builder
   */
  public ScopingBuilder requesterIDs(String... ids) {
    return this.requesterIDs(ids != null ? Arrays.asList(ids) : null);
  }

  /** {@inheritDoc} */
  @Override
  protected Class<Scoping> getObjectType() {
    return Scoping.class;
  }

}
