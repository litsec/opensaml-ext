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
