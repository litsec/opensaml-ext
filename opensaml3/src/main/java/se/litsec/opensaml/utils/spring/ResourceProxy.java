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
