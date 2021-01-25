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

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.opensaml.saml.saml2.metadata.EntityDescriptor;
import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.security.credential.UsageType;
import org.opensaml.security.x509.X509Credential;
import org.springframework.beans.factory.config.AbstractFactoryBean;

import se.litsec.opensaml.saml2.metadata.build.KeyDescriptorBuilder;

/**
 * A Spring factory bean for creating a list of {@link KeyDescriptor} objects. Useful when building metadata using a
 * Spring factory bean that creates a {@link EntityDescriptor}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class KeyDescriptorListFactoryBean extends AbstractFactoryBean<List<KeyDescriptor>> {

  /** The signature certificates to include. */
  private List<X509Certificate> signatureCertificates;

  /** The encryption certificates to include. */
  private List<X509Certificate> encryptionCertificates;

  /** Any unspecified certificates that should be included. */
  private List<X509Certificate> unspecifiedCertificates;

  /**
   * Assigns a single signing certificate to be used by giving the credential holding this certificate.
   * <p>
   * If more than one certificate should be assigned use the {@link #setSigningCredentials(List)} or
   * {@link #setSigningCertificates(List)} methods.
   * </p>
   * 
   * @param signingCredential
   *          the credential holding the signature certificate
   * @see #setSigningCertificate(X509Certificate)
   */
  public void setSigningCredential(X509Credential signingCredential) {
    this.signatureCertificates = signingCredential != null ? Collections.singletonList(
        signingCredential.getEntityCertificate()) : null;
  }

  /**
   * Assigns a single signing certificate to be used.
   * <p>
   * If more than one certificate should be assigned use the {@link #setSigningCertificates(List)} method.
   * </p>
   * 
   * @param signingCertificate
   *          the signing certificate
   */
  public void setSigningCertificate(X509Certificate signingCertificate) {
    this.signatureCertificates = signingCertificate != null ? Collections.singletonList(signingCertificate) : null;
  }

  /**
   * Assigns the signature certificates for the key descriptor. When an IdP is performing a key rollover it should
   * specify its new certificate in advance so that all relying parties have time to download the IdP metadata
   * containing the new IdP signature key. In other cases, only one signature certificate is specified (see
   * {@link #setSigningCredential(X509Credential)} or {@link #setSigningCertificate(X509Certificate)}).
   * 
   * @param signingCredentials
   *          a list of signature credentials
   * @see #setSigningCertificates(List)
   */
  public void setSigningCredentials(List<X509Credential> signingCredentials) {
    this.signatureCertificates = signingCredentials != null ? 
        signingCredentials.stream().map(X509Credential::getEntityCertificate).collect(Collectors.toList()) : null;
  }

  /**
   * Assigns the signature certificates for the key descriptor. When an IdP is performing a key rollover it should
   * specify its new certificate in advance so that all relying parties have time to download the IdP metadata
   * containing the new IdP signature key. In other cases, only one signature certificate is specified (see
   * {@link #setSigningCredential(X509Credential)} or {@link #setSigningCertificate(X509Certificate)}).
   * 
   * @param signingCertificates
   *          a list of signature certificates
   * @see #setSigningCredentials(List)
   */
  public void setSigningCertificates(List<X509Certificate> signingCertificates) {
    this.signatureCertificates = signingCertificates;
  }

  /**
   * Assign the encryption certificate to be used by giving the credential holding this certificate.
   * 
   * @param encryptionCredential
   *          the credential holding the encryption certificate
   * @see #setEncryptionCertificate(X509Certificate)
   */
  public void setEncryptionCredential(X509Credential encryptionCredential) {
    this.encryptionCertificates = encryptionCredential != null ? Collections.singletonList(
        encryptionCredential.getEntityCertificate()) : null;
  }

  /**
   * Assign the encryption certificate to be used.
   * 
   * @param encryptionCertificate
   *          the encryption certificate
   * @see #setEncryptionCredential(X509Credential)
   */
  public void setEncryptionCertificate(X509Certificate encryptionCertificate) {
    this.encryptionCertificates = encryptionCertificate != null ? Collections.singletonList(encryptionCertificate) : null;
  }

  /**
   * Should be used if more that one encryption certificate should be used in the resulting metadata. This is not a
   * common case.
   * 
   * @param encryptionCredentials
   *          a list of credentials holdning the encryption certificates
   */
  public void setEncryptionCredentials(List<X509Credential> encryptionCredentials) {
    this.encryptionCertificates = encryptionCredentials != null ? 
        encryptionCredentials.stream().map(X509Credential::getEntityCertificate).collect(Collectors.toList()) : null;
  }

  /**
   * Should be used if more that one encryption certificate should be used in the resulting metadata. This is not a
   * common case.
   * 
   * @param encryptionCertificates
   *          the encryption certificates
   */
  public void setEncryptionCertificates(List<X509Certificate> encryptionCertificates) {
    this.encryptionCertificates = encryptionCertificates;
  }

  /**
   * Should be used to add any "unspecified" certificates.
   * 
   * @param credentials
   *          a list of credentials holding the certificates to add
   */
  public void setUnspecifiedCredentials(List<X509Credential> credentials) {
    this.unspecifiedCertificates = credentials != null ? 
        credentials.stream().map(X509Credential::getEntityCertificate).collect(Collectors.toList()) : null;
  }

  /**
   * Should be used to add any "unspecified" certificates.
   * 
   * @param certificates
   *          a list of certificates to add
   */
  public void setUnspecifiedCertificates(List<X509Certificate> certificates) {
    this.unspecifiedCertificates = certificates;
  }

  /** {@inheritDoc} */
  @Override
  protected List<KeyDescriptor> createInstance() throws Exception {
    List<KeyDescriptor> keyDescriptors = new ArrayList<>();

    if (this.signatureCertificates != null) {
      keyDescriptors.addAll(this.signatureCertificates.stream()
        .map(c -> KeyDescriptorBuilder.builder().certificate(c).use(UsageType.SIGNING).build())
        .collect(Collectors.toList()));
    }
    if (this.encryptionCertificates != null) {
      keyDescriptors.addAll(this.encryptionCertificates.stream()
        .map(c -> KeyDescriptorBuilder.builder().certificate(c).use(UsageType.ENCRYPTION).build())
        .collect(Collectors.toList()));
    }
    if (this.unspecifiedCertificates != null) {
      keyDescriptors.addAll(this.unspecifiedCertificates.stream()
        .map(c -> KeyDescriptorBuilder.builder().certificate(c).use(UsageType.UNSPECIFIED).build())
        .collect(Collectors.toList()));
    }

    return keyDescriptors;
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getObjectType() {
    return List.class;
  }

}
