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

import java.io.InputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;

import org.opensaml.saml.saml2.metadata.KeyDescriptor;
import org.opensaml.security.credential.UsageType;
import org.opensaml.xmlsec.signature.KeyInfo;
import org.opensaml.xmlsec.signature.KeyName;
import org.opensaml.xmlsec.signature.X509Data;

import se.litsec.opensaml.core.AbstractSAMLObjectBuilder;
import se.litsec.opensaml.utils.ObjectUtils;
import se.litsec.opensaml.utils.X509CertificateUtils;

/**
 * A builder for {@code KeyDescriptor} elements.
 * <p>
 * This builder only supports a subset of the possible elements of a key descriptor, but should be sufficient for most
 * cases.
 * </p>
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class KeyDescriptorBuilder extends AbstractSAMLObjectBuilder<KeyDescriptor> {

  /**
   * Utility method that creates a builder.
   * 
   * @return a builder
   */
  public static KeyDescriptorBuilder builder() {
    return new KeyDescriptorBuilder();
  }

  /** {@inheritDoc} */
  @Override
  protected Class<KeyDescriptor> getObjectType() {
    return KeyDescriptor.class;
  }

  /**
   * Assigns the usage type for the key descriptor.
   * 
   * @param usageType
   *          the usage type
   * @return the builder
   */
  KeyDescriptorBuilder use(UsageType usageType) {
    if (UsageType.UNSPECIFIED.equals(usageType)) {
      this.object().setUse(null);
    }
    else {
      this.object().setUse(usageType);
    }
    return this;
  }

  /**
   * Assigns the key name of the {@code KeyInfo} element within the key descriptor.
   * 
   * @param name
   *          the key name
   * @return the builder
   */
  KeyDescriptorBuilder keyName(String name) {
    if (name == null) {
      if (this.object().getKeyInfo() != null && !this.object().getKeyInfo().getKeyNames().isEmpty()) {
        this.object().getKeyInfo().getKeyNames().clear();
      }
    }
    if (this.object().getKeyInfo() == null) {
      this.object().setKeyInfo(ObjectUtils.createXMLObject(KeyInfo.class, KeyInfo.DEFAULT_ELEMENT_NAME));
    }
    this.object().getKeyInfo().getKeyNames().clear();
    KeyName keyName = ObjectUtils.createXMLObject(KeyName.class, KeyName.DEFAULT_ELEMENT_NAME);
    keyName.setValue(name);
    this.object().getKeyInfo().getKeyNames().add(keyName);
    return this;
  }

  /**
   * Assigns a certificate to be used as a X.509 data element of the {@code KeyInfo} element within the key descriptor.
   * 
   * @param certificate
   *          the certificate
   * @return the builder
   */
  KeyDescriptorBuilder certificate(X509Certificate certificate) {
    try {
      return this.certificate(
        certificate != null ? Base64.getEncoder().encodeToString(certificate.getEncoded()) : null);
    }
    catch (CertificateEncodingException e) {
      throw new SecurityException(e);
    }
  }

  /**
   * Assigns an input stream to a certificate resource that is to be used as a X.509 data element of the {@code KeyInfo}
   * element within the key descriptor.
   * 
   * @param certificate
   *          the certificate resource
   * @return the builder
   */
  KeyDescriptorBuilder certificate(InputStream certificate) {
    if (certificate == null) {
      return this;
    }

    try {
      return this.certificate(
        Base64.getEncoder().encodeToString(X509CertificateUtils.decodeCertificate(certificate).getEncoded()));
    }
    catch (CertificateException e) {
      throw new SecurityException(e);
    }
  }

  /**
   * Assigns a certificate (in Base64-encoded format) to be used as a X.509 data element of the {@code KeyInfo} element
   * within the key descriptor.
   * 
   * @param base64Encoding
   *          the base64 encoding (note: not PEM-format)
   * @return the builder
   */
  KeyDescriptorBuilder certificate(String base64Encoding) {
    if (base64Encoding == null) {
      if (this.object().getKeyInfo() != null && !this.object().getKeyInfo().getX509Datas().isEmpty()) {
        this.object().getKeyInfo().getX509Datas().clear();
      }
    }
    if (this.object().getKeyInfo() == null) {
      this.object().setKeyInfo(ObjectUtils.createXMLObject(KeyInfo.class, KeyInfo.DEFAULT_ELEMENT_NAME));
    }
    this.object().getKeyInfo().getX509Datas().clear();
    X509Data x509Data = ObjectUtils.createXMLObject(X509Data.class, X509Data.DEFAULT_ELEMENT_NAME);
    org.opensaml.xmlsec.signature.X509Certificate cert = ObjectUtils.createXMLObject(
      org.opensaml.xmlsec.signature.X509Certificate.class, org.opensaml.xmlsec.signature.X509Certificate.DEFAULT_ELEMENT_NAME);
    cert.setValue(base64Encoding);
    x509Data.getX509Certificates().add(cert);
    this.object().getKeyInfo().getX509Datas().add(x509Data);
    return this;
  }

}
