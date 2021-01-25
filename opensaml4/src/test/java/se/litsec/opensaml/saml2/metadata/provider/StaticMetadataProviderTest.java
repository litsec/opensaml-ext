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
package se.litsec.opensaml.saml2.metadata.provider;

import org.opensaml.core.xml.XMLObject;
import org.opensaml.core.xml.config.XMLObjectProviderRegistrySupport;
import org.opensaml.core.xml.util.XMLObjectSupport;
import org.springframework.core.io.Resource;
import org.w3c.dom.Element;

/**
 * Test cases for the {@code StaticMetadataProvider}.
 * <p>
 * See {@link BaseMetadataProviderTest} for test cases.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class StaticMetadataProviderTest extends BaseMetadataProviderTest {

  /** {@inheritDoc} */
  @Override
  protected AbstractMetadataProvider createMetadataProvider(Resource resource) throws Exception {
    
    XMLObject object = XMLObjectSupport.unmarshallFromInputStream(XMLObjectProviderRegistrySupport.getParserPool(), resource.getInputStream());
    Element dom = object.getDOM();
    if (dom == null) {
      dom = XMLObjectSupport.marshall(object);
    }
    return new StaticMetadataProvider(dom);
  }

}
