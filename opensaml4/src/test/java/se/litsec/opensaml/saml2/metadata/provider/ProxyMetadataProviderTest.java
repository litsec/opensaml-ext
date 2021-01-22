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
package se.litsec.opensaml.saml2.metadata.provider;

import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.saml.metadata.resolver.impl.ResourceBackedMetadataResolver;
import org.springframework.core.io.Resource;

import se.litsec.opensaml.utils.spring.ResourceProxy;

/**
 * Test cases for the {@code ProxyMetadataProvider}.
 * <p>
 * See {@link BaseMetadataProviderTest} for test cases.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ProxyMetadataProviderTest extends BaseMetadataProviderTest {

  /** {@inheritDoc} */
  @Override
  protected AbstractMetadataProvider createMetadataProvider(Resource resource) throws Exception {
    ResourceBackedMetadataResolver resolver = new ResourceBackedMetadataResolver(ResourceProxy.proxy(resource));
    resolver.setParserPool(XMLObjectProviderRegistrySupport.getParserPool());
    resolver.setId(resource.getFilename());
    return new ProxyMetadataProvider(resolver);
  }

}
