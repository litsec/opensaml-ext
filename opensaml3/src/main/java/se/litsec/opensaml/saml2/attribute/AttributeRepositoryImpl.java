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
