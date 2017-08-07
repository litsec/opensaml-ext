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
import se.litsec.opensaml.utils.ObjectUtils;

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
   *          the {@code GetComplete} element of the {@code IDPList} element
   * @param idpEntries
   *          the {@code IDPEntry} elements of the {@code IDPList} element
   * @return the builder
   */
  public ScopingBuilder idpList(String completeUri, List<IDPEntry> idpEntries) {
    if (completeUri == null && (idpEntries == null || idpEntries.isEmpty())) {
      this.object().setIDPList(null);
    }
    else {
      IDPList idpList = ObjectUtils.createSamlObject(IDPList.class);
      GetComplete getComplete = ObjectUtils.createSamlObject(GetComplete.class);
      getComplete.setGetComplete(completeUri);
      idpList.setGetComplete(getComplete);
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
   *          the {@code GetComplete} element of the {@code IDPList} element
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
    IDPEntry entry = ObjectUtils.createSamlObject(IDPEntry.class);
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
        RequesterID ri = ObjectUtils.createSamlObject(RequesterID.class);
        ri.setRequesterID(id);
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
