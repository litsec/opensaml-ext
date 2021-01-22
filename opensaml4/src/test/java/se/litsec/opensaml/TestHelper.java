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
package se.litsec.opensaml;

import java.util.Properties;

import org.springframework.util.StringUtils;

import se.litsec.opensaml.core.LocalizedString;

/**
 * Helper methods for testing.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class TestHelper {

  // Hidden
  private TestHelper() {
  }
  
  public static LocalizedString getLocalizedString(Properties p, String key) {
    String s = p.getProperty(key);
    if (!StringUtils.hasText(s)) {
      return null;
    }
    return new LocalizedString(s.trim());
  }

  public static String[] getStringArray(Properties p, String key) {
    String s = p.getProperty(key);
    if (!StringUtils.hasText(s)) {
      return null;
    }
    String[] arr = s.trim().split(",");
    String[] larr = new String[arr.length];
    for (int i = 0; i < arr.length; i++) {
      larr[i] = arr[i].trim(); 
    }
    return larr;
  }

  public static LocalizedString[] getLocalizedStringArray(Properties p, String key) {
    String s = p.getProperty(key);
    if (!StringUtils.hasText(s)) {
      return null;
    }
    String[] arr = s.trim().split(",");
    LocalizedString[] larr = new LocalizedString[arr.length];
    for (int i = 0; i < arr.length; i++) {
      larr[i] = new LocalizedString(arr[i]); 
    }
    return larr;
  }

}
