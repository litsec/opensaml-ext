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

import java.io.IOException;
import java.util.List;

import org.opensaml.core.xml.io.MarshallingException;
import org.opensaml.core.xml.io.UnmarshallingException;
import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.SingleSignOnService;
import org.springframework.core.io.Resource;

import net.shibboleth.utilities.java.support.xml.XMLParserException;
import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.saml2.metadata.build.AbstractEntityDescriptorBuilder;
import se.litsec.opensaml.saml2.metadata.build.IdpEntityDescriptorBuilder;

/**
 * A Spring factory bean for creating {@link EntityDescriptor} objects for Identity Provider metadata using setter
 * methods, and optionally a template object.
 * <p>
 * When a template object is used, the factory is initialized using the
 * {@link AbstractEntityDescriptorBuilder#AbstractEntityDescriptorBuilder(java.io.InputStream)} or
 * {@link AbstractEntityDescriptorBuilder#AbstractEntityDescriptorBuilder(EntityDescriptor)} constructors. The user may
 * later change, or add, any of the elements and attributes of the template object using the assignment methods.
 * </p>
 * <p>
 * Note that no Signature will be included.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 * @see IdpEntityDescriptorBuilder
 */
public class IdpEntityDescriptorFactoryBean extends AbstractEntityDescriptorFactoryBean<IdpEntityDescriptorBuilder> {

  /** The builder. */
  private IdpEntityDescriptorBuilder builder;

  /**
   * Constructor setting up the factory with no template. This means that the entire {@code EntityDescriptor} object is
   * created from data assigned using set methods.
   * 
   * @see IdpEntityDescriptorBuilder#IdpEntityDescriptorBuilder()
   */
  public IdpEntityDescriptorFactoryBean() {
    this.builder = new IdpEntityDescriptorBuilder();
  }

  /**
   * Constructor setting up the factory with a template {@code EntityDescriptor} that is read from a resource. Users of
   * the bean may now change, add or delete (by using {@code null} values), the elements and attributes of the template
   * object using the setter methods.
   * 
   * @param resource
   *          the template resource
   * @throws IOException
   *           if the resource can not be read
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws XMLParserException
   *           for XML parsing errors
   */
  public IdpEntityDescriptorFactoryBean(Resource resource) throws XMLParserException, UnmarshallingException, IOException {
    this.builder = new IdpEntityDescriptorBuilder(resource.getInputStream());
  }

  /**
   * Constructor setting up the factory with a template {@code EntityDescriptor}. Users of the bean may now change, add
   * or delete (by using {@code null} values), the elements and attributes of the template object using the setter
   * methods.
   * 
   * @param template
   *          the template
   * @throws UnmarshallingException
   *           for unmarshalling errors
   * @throws MarshallingException
   *           for marshalling errors
   */
  public IdpEntityDescriptorFactoryBean(EntityDescriptor template) throws UnmarshallingException, MarshallingException {
    this.builder = new IdpEntityDescriptorBuilder(template);
  }

  /**
   * Assigns the {@code WantAuthnRequestsSigned} attribute of the {@code md:IDPSSODescriptor} element.
   * 
   * @param b
   *          boolean (if {@code null}, the attribute is not set)
   */
  public void setWantAuthnRequestsSigned(Boolean b) {
    this.builder.wantAuthnRequestsSigned(b);
  }

  /**
   * Adds a set of URIs to the assurance certification attribute (
   * {@code urn:oasis:names:tc:SAML:attribute:assurance-certification}) that is part of the
   * {@code mdattr:EntityAttributes} element that is part of the metadata extension element.
   * 
   * @param uris
   *          the assurance URI values that should be added
   * @see #setEntityAttributesExtension(List)
   */
  public void setAssuranceCertificationUris(List<String> uris) {
    this.builder.assuranceCertificationUris(uris);
  }

  /**
   * Adds {@code md:SingleSignOnService} elements to the {@code IDPSSODescriptor}.
   * 
   * @param singleSignOnServices
   *          single sign on service objects (cloned before assignment)
   */
  public void setSingleSignOnServices(List<SingleSignOnService> singleSignOnServices) {
    this.builder.singleSignOnServices(singleSignOnServices);
  }

  /** {@inheritDoc} */
  @Override
  protected IdpEntityDescriptorBuilder _builder() {
    return this.builder;
  }

  /** {@inheritDoc} */
  @Override
  protected AbstractSAMLObjectBuilder<EntityDescriptor> builder() {
    return this.builder;
  }

}
