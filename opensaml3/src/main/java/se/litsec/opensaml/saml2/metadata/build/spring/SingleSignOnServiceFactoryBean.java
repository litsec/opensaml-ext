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

import org.opensaml.saml.saml2.metadata.SingleSignOnService;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.core.spring.AbstractSAMLObjectBuilderFactoryBean;
import se.litsec.opensaml.saml2.metadata.build.SingleSignOnServiceBuilder;

/**
 * A Spring factory bean for creating {@link SingleSignOnService} objects.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see SingleSignOnServiceBuilder
 */
public class SingleSignOnServiceFactoryBean extends AbstractSAMLObjectBuilderFactoryBean<SingleSignOnService> {

  /** The builder. */
  private SingleSignOnServiceBuilder builder;

  /**
   * Default constructor.
   */
  public SingleSignOnServiceFactoryBean() {
    this.builder = SingleSignOnServiceBuilder.builder();
  }

  /**
   * Assigns the binding URI.
   * 
   * @param binding
   *          the binding URI
   */
  public void setBinding(String binding) {
    this.builder.binding(binding);
  }
  
  public void setProtocolBinding(final String binding) {
    this.builder.protocolBinding(binding);
  }

  /**
   * Assigns the location URL.
   * 
   * @param location
   *          the location URL
   */
  public void setLocation(String location) {
    this.builder.location(location);
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<SingleSignOnService> builder() {
    return this.builder;
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return SingleSignOnService.class;
  }

}
