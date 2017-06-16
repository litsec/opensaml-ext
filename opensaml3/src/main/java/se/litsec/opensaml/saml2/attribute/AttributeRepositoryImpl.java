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
import java.util.stream.Collectors;

/**
 * A bean implementing the {@code AttributeRepository} interface.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AttributeRepositoryImpl implements AttributeRepository {

  /** The attributes. */
  private List<AttributeTemplate> attributes;

  /**
   * Constructor.
   * 
   * @param attributes
   *          the attributes stored by the repository
   */
  public AttributeRepositoryImpl(List<AttributeTemplate> attributes) {
    if (attributes == null) {
      throw new IllegalArgumentException("attributes must not be null");
    }
    this.attributes = attributes;
  }

  /** {@inheritDoc} */
  @Override
  public List<String> getAttributeNames() {
    return this.attributes.stream().map(AttributeTemplate::getName).collect(Collectors.toList());
  }

  /** {@inheritDoc} */
  @Override
  public AttributeTemplate getByName(String name) {
    return this.attributes.stream().filter(a -> name.equals(a.getName())).findFirst().orElse(null);
  }

  /** {@inheritDoc} */
  @Override
  public AttributeTemplate getByFriendlyName(String friendlyName) {
    return this.attributes.stream().filter(a -> friendlyName.equals(a.getFriendlyName())).findFirst().orElse(null);
  }

}
