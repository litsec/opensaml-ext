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
package se.litsec.opensaml.saml2.metadata.build.spring;

import org.opensaml.saml.saml2.metadata.AssertionConsumerService;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.AssertionConsumerServiceBuilder;

/**
 * A Spring factory bean for creating {@link AssertionConsumerService} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class AssertionConsumerServiceFactoryBean extends AbstractSAMLObjectBuilderFactoryBean<AssertionConsumerService> {

  /** The builder. */
  private AssertionConsumerServiceBuilder builder;

  /**
   * Constructor.
   */
  public AssertionConsumerServiceFactoryBean() {
    this.builder = new AssertionConsumerServiceBuilder();
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return AssertionConsumerService.class;
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<AssertionConsumerService> builder() {
    return this.builder;
  }

  /**
   * Assigns the location URI.
   * 
   * @param location
   *          the URI
   * @see AssertionConsumerServiceBuilder#location(String)
   */
  public void setLocation(String location) {
    this.builder.location(location);
  }

  /**
   * Assigns the binding of the service
   * 
   * @param binding
   *          the binding URI
   * @see AssertionConsumerServiceBuilder#binding(String)
   */
  public void setBinding(String binding) {
    this.builder.binding(binding);
  }

  /**
   * Assigns the index for the service.
   * 
   * @param index
   *          the index
   * @see AssertionConsumerServiceBuilder#index(Integer)
   */
  public void setIndex(Integer index) {
    this.builder.index(index);
  }

  /**
   * Sets the {@code isDefault} attribute of the service.
   * 
   * @param isDefault
   *          the Boolean
   * @see AssertionConsumerServiceBuilder#isDefault(Boolean)
   */
  public void setIsDefault(Boolean isDefault) {
    this.builder.isDefault(isDefault);
  }

}
