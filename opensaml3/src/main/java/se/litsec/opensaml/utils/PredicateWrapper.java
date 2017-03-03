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
package se.litsec.opensaml.utils;

import java.util.function.Predicate;

/**
 * OpenSAML uses {@code com.google.common.base.Predicate} but we want to use {@link java.util.function.Predicate}. This
 * wrapper class encapsulates a {@code java.util.function.Predicate} into a {@code com.google.common.base.Predicate}
 * instance.
 * 
 * @author Martin Lindström (martin.lindstrom@litsec.se)
 *
 * @param <T>
 *          the type
 */
public class PredicateWrapper<T> implements com.google.common.base.Predicate<T> {

  /** The wrapped {@code java.util.function.Predicate}. */
  private Predicate<T> predicate;

  /**
   * Constructor assigning the {@code java.util.function.Predicate} to wrap.
   * 
   * @param predicate
   *          the predicate
   */
  public PredicateWrapper(Predicate<T> predicate) {
    this.predicate = predicate;
  }

  /** {@inheritDoc} */
  @Override
  public boolean apply(T input) {
    return this.predicate.test(input);
  }

  /**
   * Utility method that wraps a {@link java.util.function.Predicate} into a {@code com.google.common.base.Predicate}.
   * 
   * @param predicate
   *          the predicate to wrap
   * @return a {@code com.google.common.base.Predicate} instance
   */
  public static <T> com.google.common.base.Predicate<T> wrap(Predicate<T> predicate) {
    return new PredicateWrapper<T>(predicate);
  }

}
