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
   * @param <T>
   *          the type contained in the predicate
   * @return a {@code com.google.common.base.Predicate} instance
   */
  public static <T> com.google.common.base.Predicate<T> wrap(Predicate<T> predicate) {
    return new PredicateWrapper<>(predicate);
  }

}
