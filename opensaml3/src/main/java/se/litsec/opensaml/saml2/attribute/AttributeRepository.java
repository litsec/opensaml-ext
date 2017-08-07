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
package se.litsec.opensaml.saml2.attribute;

import java.util.List;

/**
 * An interface for a repository of attribute definitions.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public interface AttributeRepository {

  /**
   * Returns a list of all attribute names that are stored in the repository.
   * 
   * @return a list of attribute names
   */
  List<String> getAttributeNames();

  /**
   * Returns the {@code AttributeTemplate} for the given attribute name.
   * 
   * @param name
   *          the attribute name
   * @return the matching {@code AttributeTemplate} or {@code null} if no match exists
   */
  AttributeTemplate getByName(String name);

  /**
   * Returns the {@code AttributeTemplate} for the given friendly name.
   * 
   * @param friendlyName
   *          the attribute friendly name
   * @return the matching {@code AttributeTemplate} or {@code null} if no match exists
   */
  AttributeTemplate getByFriendlyName(String friendlyName);

}
