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
package se.litsec.opensaml.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Utilities for handling X.509 certificates.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class X509CertificateUtils {

  /** Factory for creating certificates. */
  private static CertificateFactory factory = null;

  static {
    try {
      factory = CertificateFactory.getInstance("X.509");
    }
    catch (CertificateException e) {
      throw new SecurityException(e);
    }
  }

  /**
   * Given a file containing a X.509 certificate the method returns a {@link X509Certificate} object.
   * 
   * @param file
   *          the file
   * @return a {@link X509Certificate} object
   * @throws CertificateException
   *           for decoding errors
   * @throws FileNotFoundException
   *           if the file cannot be found
   */
  public static X509Certificate decodeCertificate(File file) throws CertificateException, FileNotFoundException {
    return decodeCertificate(new FileInputStream(file));
  }

  /**
   * Decodes a {@link X509Certificate} from an input stream.
   * 
   * @param stream
   *          the stream to read
   * @return a {@link X509Certificate} object
   * @throws CertificateException
   *           for decoding errors
   */
  public static X509Certificate decodeCertificate(InputStream stream) throws CertificateException {
    return (X509Certificate) factory.generateCertificate(stream);
  }

  // Hidden constructor
  private X509CertificateUtils() {
  }

}
