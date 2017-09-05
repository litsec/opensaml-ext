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
package se.litsec.opensaml.saml2.metadata.build;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
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
    this.signatureCertificates = signingCredential != null ? Arrays.asList(signingCredential.getEntityCertificate()) : null;
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
    this.signatureCertificates = signingCertificate != null ? Arrays.asList(signingCertificate) : null;
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
        signingCredentials.stream().map(c -> c.getEntityCertificate()).collect(Collectors.toList()) : null;
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
    this.signatureCertificates = signingCertificates != null ? signingCertificates : null;
  }

  /**
   * Assign the encryption certificate to be used by giving the credential holding this certificate.
   * 
   * @param encryptionCredential
   *          the credential holding the encryption certificate
   * @see #setEncryptionCertificate(X509Certificate)
   */
  public void setEncryptionCredential(X509Credential encryptionCredential) {
    this.encryptionCertificates = encryptionCredential != null ? Arrays.asList(encryptionCredential.getEntityCertificate()) : null;
  }

  /**
   * Assign the encryption certificate to be used.
   * 
   * @param encryptionCertificate
   *          the encryption certificate
   * @see #setEncryptionCredential(X509Credential)
   */
  public void setEncryptionCertificate(X509Certificate encryptionCertificate) {
    this.encryptionCertificates = encryptionCertificate != null ? Arrays.asList(encryptionCertificate) : null;
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
        encryptionCredentials.stream().map(c -> c.getEntityCertificate()).collect(Collectors.toList()) : null;
  }

  /**
   * Should be used if more that one encryption certificate should be used in the resulting metadata. This is not a
   * common case.
   * 
   * @param encryptionCertificates
   *          the encryption certificates
   */
  public void setEncryptionCertificates(List<X509Certificate> encryptionCertificates) {
    this.encryptionCertificates = encryptionCertificates != null ? encryptionCertificates : null;
  }

  /**
   * Should be used to add any "unspecified" certificates.
   * 
   * @param credentials
   *          a list of credentials holding the certificates to add
   */
  public void setUnspecifiedCredentials(List<X509Credential> credentials) {
    this.unspecifiedCertificates = credentials != null ? 
        credentials.stream().map(c -> c.getEntityCertificate()).collect(Collectors.toList()) : null;
  }

  /**
   * Should be used to add any "unspecified" certificates.
   * 
   * @param certificates
   *          a list of certificates to add
   */
  public void setUnspecifiedCertificates(List<X509Certificate> certificates) {
    this.unspecifiedCertificates = certificates != null ? certificates : null;
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
