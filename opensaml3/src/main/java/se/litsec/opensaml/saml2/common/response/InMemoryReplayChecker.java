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
package se.litsec.opensaml.saml2.common.response;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.shibboleth.utilities.java.support.annotation.Duration;

/**
 * An in-memory based message replay checker implementation. This is mainly for testing and simple mock implementations.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class InMemoryReplayChecker implements MessageReplayChecker {
  
  /** If the size exceeds MAX_SIZE we clean up the cache map. */
  private static final int MAX_SIZE = 1000;
  
  /** Logging instance. */
  private final Logger log = LoggerFactory.getLogger(InMemoryReplayChecker.class);
  
  /** Number of milliseconds to keep elements in the replay cache - default is 5 minutes. */
  @Duration 
  private long replayCacheExpiration = 300 * 1000L;
  
  /** The cache. */
  private Map<String, Long> cache = new ConcurrentHashMap<>();

  /** {@inheritDoc} */
  @Override
  public synchronized void checkReplay(String id) throws MessageReplayException {
    Long e = this.cache.get(id);
    if (e == null) {
      this.cache.put(id, replayCacheExpiration + System.currentTimeMillis());
    }
    else {
      if (System.currentTimeMillis() > e) {
        this.cache.remove(id);
        String msg = String.format("Replay check of ID '%s' failed", id);
        log.warn(msg);
        throw new MessageReplayException(msg);
      }
    }
    log.debug("Message replay check of ID '{}' succeeded", id);
    if (this.cache.size() > MAX_SIZE) {
      long now = System.currentTimeMillis();
      this.cache.entrySet().removeIf(entry -> now > entry.getValue());
    }
  }

  /** {@inheritDoc} */
  @Override
  public void checkReplay(SAMLObject object) throws MessageReplayException, IllegalArgumentException {
    String id = null;
    if (object instanceof Response) {
      id = ((Response) object).getID();
    }
    else if (object instanceof Assertion) {
      id = ((Assertion) object).getID();
    }
    if (id == null) {
      throw new IllegalArgumentException("Unsupported object type");
    }
    this.checkReplay(id);
  }
    
  /**
   * Assigns the number of milliseconds each stored ID should be kept in the cache. The default is 5 minutes.
   * 
   * @param replayCacheExpiration
   *          number of millis
   */
  @Duration
  public void setReplayCacheExpiration(long replayCacheExpiration) {
    if (replayCacheExpiration < 0) {
      throw new IllegalArgumentException("replayCacheExpiration must be greater than 0");
    }
    this.replayCacheExpiration = replayCacheExpiration;
  }
  
}
