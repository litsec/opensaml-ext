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
package se.litsec.opensaml.saml2.metadata.provider;

import java.util.function.Predicate;

import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.IDPSSODescriptor;
import org.opensaml.saml.saml2.metadata.SPSSODescriptor;

/**
 * A number of predicates that may be installed as filters for a metadata provider.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class MetadataProviderPredicates {

  /**
   * Returns a predicate that evaluates to {@code true} if the supplied entity descriptor represents an IdP.
   * 
   * @return predicate for filtering IdPs
   */
  public static Predicate<EntityDescriptor> includeOnlyIDPs() {
    return e -> isIDP(e);
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the supplied entity descriptor represents an IdP or if the
   * entity descriptor is "my" entry (typically the SP).
   * 
   * @param entityID
   *          the entityID for the entity descriptor to include even if it's not an IdP
   * @return predicate for filtering IdPs and "my" entity
   */
  public static Predicate<EntityDescriptor> includeOnlyIDPsAndMe(String entityID) {
    return e -> entityID.equals(e.getEntityID()) || isIDP(e);
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the supplied entity descriptor represents an SP.
   * 
   * @return predicate for filtering SPs
   */
  public static Predicate<EntityDescriptor> includeOnlySPs() {
    return e -> !e.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).isEmpty();
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the supplied entity descriptor is an IdP and that it contains
   * entity categories that matches the entity categories in the SP entity descriptor ({@code sp}).
   * 
   * @param sp
   *          the SP entity descriptor
   * @param includeMyself
   *          should the SP entity descriptor be included as well as matching IdPs?
   * @return predicate for filtering matching IdPs
   */
  public static Predicate<EntityDescriptor> includeOnlyUsableIdPs(EntityDescriptor sp, boolean includeMyself) {
    return e -> (includeMyself && sp.getEntityID().equals(e.getEntityID())) || (isIDP(e) && isMatchingIDP(sp, e));
  }

  /**
   * Tells whether the supplied entity descriptor is an IdP.
   * 
   * @param ed
   *          the entity descriptor to test
   * @return {@code true} if the entity descriptor represents an IdP and {@code false} otherwise
   */
  public static boolean isIDP(EntityDescriptor ed) {
    return !ed.getRoleDescriptors(IDPSSODescriptor.DEFAULT_ELEMENT_NAME).isEmpty();
  }

  /**
   * Tells whether the supplied entity descriptor is an SP.
   * 
   * @param ed
   *          the entity descriptor to test
   * @return {@code true} if the entity descriptor represents an SP and {@code false} otherwise
   */  
  public static boolean isSP(EntityDescriptor ed) {
    return !ed.getRoleDescriptors(SPSSODescriptor.DEFAULT_ELEMENT_NAME).isEmpty();
  }

  public static boolean isMatchingIDP(EntityDescriptor sp, EntityDescriptor idp) {
    // TODO
    return true;
  }

  // Hidden constructor.
  private MetadataProviderPredicates() {
  }

}
