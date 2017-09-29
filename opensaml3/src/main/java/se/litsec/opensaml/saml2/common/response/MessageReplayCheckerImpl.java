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
package se.litsec.opensaml.saml2.common.response;

import org.opensaml.saml.common.SAMLObject;
import org.opensaml.saml.saml2.core.Assertion;
import org.opensaml.saml.saml2.core.Response;
import org.opensaml.storage.ReplayCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import net.shibboleth.utilities.java.support.annotation.Duration;

/**
 * Message replay checker implementation using OpenSAML's {@link ReplayCache} as an underlying cache.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class MessageReplayCheckerImpl implements MessageReplayChecker, InitializingBean {

  /** Logging instance. */
  private final Logger log = LoggerFactory.getLogger(MessageReplayCheckerImpl.class);

  /** The replay cache. */
  private ReplayCache replayCache;

  /** Number of milliseconds to keep elements in the replay cache - default is 5 minutes. */
  @Duration 
  private long replayCacheExpiration = 300 * 1000L;

  /** The name of the replay cache. */
  private String replayCacheName;

  /** {@inheritDoc} */
  @Override
  public void checkReplay(String id) throws MessageReplayException {
    if (!this.replayCache.check(this.replayCacheName, id, this.replayCacheExpiration)) {
      String msg = String.format("Replay check of ID '%s' failed", id);
      log.warn(msg);
      throw new MessageReplayException(msg);
    }
    log.debug("Message replay check of ID '{}' succeeded", id);
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
   * Assigns the replay cache to use when checking against replay attacks.
   * 
   * @param replayCache
   *          the cache
   */
  public void setReplayCache(ReplayCache replayCache) {
    this.replayCache = replayCache;
  }

  /**
   * Assigns the name of the replay cache.
   * 
   * @param replayCacheName
   *          the name
   */
  public void setReplayCacheName(String replayCacheName) {
    this.replayCacheName = replayCacheName;
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

  /** {@inheritDoc} */
  @Override
  public void afterPropertiesSet() throws Exception {
    Assert.notNull(this.replayCache, "Property 'replayCache' must be assigned");
    Assert.hasText(this.replayCacheName, "Property 'replayCacheName' must be assigned");
  }

}
