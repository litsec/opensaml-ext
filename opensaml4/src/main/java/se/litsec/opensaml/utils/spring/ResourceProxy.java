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
package se.litsec.opensaml.utils.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * An invocation handler that is used to proxy from a OpenSAML
 * {@link net.shibboleth.utilities.java.support.resource.Resource} to a Spring
 * {@link org.springframework.core.io.Resource}.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 */
public class ResourceProxy implements InvocationHandler {

  /** The underlying Spring resource. */
  private org.springframework.core.io.Resource resource;

  /**
   * Creates a proxy that proxies calls to the OpenSAML {@code Resource} interface to an implementation of a Spring
   * {@code Resource} interface.
   * 
   * @param resource
   *          the target resource (Spring)
   * @return a proxy implementing the OpenSAML {@link net.shibboleth.utilities.java.support.resource.Resource} interface
   */
  public static net.shibboleth.utilities.java.support.resource.Resource proxy(org.springframework.core.io.Resource resource) {
    return (net.shibboleth.utilities.java.support.resource.Resource) Proxy.newProxyInstance(
      net.shibboleth.utilities.java.support.resource.Resource.class.getClassLoader(), new Class<?>[] {
          net.shibboleth.utilities.java.support.resource.Resource.class }, new ResourceProxy(resource));
  }

  /**
   * Constructor assigning the Spring {@code Resource} instance.
   * 
   * @param resource
   *          Spring {@code Resource} instance
   */
  private ResourceProxy(org.springframework.core.io.Resource resource) {
    this.resource = resource;
  }

  /** {@inheritDoc} */
  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Method targetMethod = this.resource.getClass().getMethod(method.getName(), method.getParameterTypes());
    return targetMethod.invoke(this.resource, args);
  }

}
