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
package se.litsec.opensaml.saml2.metadata.provider.spring;

import java.io.IOException;

import org.springframework.core.io.Resource;

import se.litsec.opensaml.saml2.metadata.provider.FilesystemMetadataProvider;

/**
 * Utility class that accepts a Spring Framework {@link org.springframework.core.io.Resource} as the metadata source.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class SpringResourceMetadataProvider extends FilesystemMetadataProvider {

  /**
   * Constructor taking a Spring Framework {@link org.springframework.core.io.Resource} as the metadata source.
   * 
   * @param metadataResource
   *          the metadata source
   * @throws IOException
   *           if the given resource can not be represented as a {@code File} object
   */
  public SpringResourceMetadataProvider(Resource metadataResource) throws IOException {
    super(metadataResource.getFile());
  }

}
