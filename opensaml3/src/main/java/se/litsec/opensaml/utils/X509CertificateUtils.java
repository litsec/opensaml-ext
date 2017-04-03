/*
 * The swedish-eid-opensaml project is an open-source package that extends OpenSAML
 * with functions for the Swedish eID Framework.
 *
 * More details on <https://github.com/litsec/swedish-eid-opensaml>
 * Copyright (C) 2016 Litsec AB
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
package se.litsec.opensaml.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
   * Given a certificate holding a X.509 certificate the method returns a {@link X509Certificate} object.
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
    return (X509Certificate) factory.generateCertificate(new FileInputStream(file));
  }

  // Hidden constructor
  private X509CertificateUtils() {
  }

}
