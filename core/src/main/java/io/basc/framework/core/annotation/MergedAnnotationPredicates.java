/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.ObjectUtils;

/**
 * Predicate implementations that provide various test operations for
 * {@link MergedAnnotation MergedAnnotations}.
 *
 * @author Phillip Webb
 */
public abstract class MergedAnnotationPredicates {

	private MergedAnnotationPredicates() {
	}

	/**
	 * Create a new {@link Predicate} that evaluates to {@code true} if the name of
	 * the {@linkplain MergedAnnotation#getType() merged annotation type} is
	 * contained in the specified array.
	 * 
	 * @param <A>       the annotation type
	 * @param typeNames the names that should be matched
	 * @return a {@link Predicate} to test the annotation type
	 */
	public static <A extends Annotation> Predicate<MergedAnnotation<? extends A>> typeIn(String... typeNames) {
		return annotation -> ObjectUtils.containsElement(typeNames, annotation.getType().getName());
	}

	/**
	 * Create a new {@link Predicate} that evaluates to {@code true} if the
	 * {@linkplain MergedAnnotation#getType() merged annotation type} is contained
	 * in the specified array.
	 * 
	 * @param <A>   the annotation type
	 * @param types the types that should be matched
	 * @return a {@link Predicate} to test the annotation type
	 */
	public static <A extends Annotation> Predicate<MergedAnnotation<? extends A>> typeIn(Class<?>... types) {
		return annotation -> ObjectUtils.containsElement(types, annotation.getType());
	}

	/**
	 * Create a new {@link Predicate} that evaluates to {@code true} if the
	 * {@linkplain MergedAnnotation#getType() merged annotation type} is contained
	 * in the specified collection.
	 * 
	 * @param <A>   the annotation type
	 * @param types the type names or classes that should be matched
	 * @return a {@link Predicate} to test the annotation type
	 */
	public static <A extends Annotation> Predicate<MergedAnnotation<? extends A>> typeIn(Collection<?> types) {
		return annotation -> types.stream()
				.map(type -> type instanceof Class ? ((Class<?>) type).getName() : type.toString())
				.anyMatch(typeName -> typeName.equals(annotation.getType().getName()));
	}

	public static <A extends Annotation> Predicate<MergedAnnotation<A>> firstRunOf(
			Function<? super MergedAnnotation<A>, ?> valueExtractor) {

		return new FirstRunOfPredicate<>(valueExtractor);
	}

	public static <A extends Annotation, K> Predicate<MergedAnnotation<A>> unique(
			Function<? super MergedAnnotation<A>, K> keyExtractor) {

		return new UniquePredicate<>(keyExtractor);
	}

	/**
	 * {@link Predicate} implementation used for
	 * {@link MergedAnnotationPredicates#firstRunOf(Function)}.
	 */
	private static class FirstRunOfPredicate<A extends Annotation> implements Predicate<MergedAnnotation<A>> {

		private final Function<? super MergedAnnotation<A>, ?> valueExtractor;

		private boolean hasLastValue;

		@Nullable
		private Object lastValue;

		FirstRunOfPredicate(Function<? super MergedAnnotation<A>, ?> valueExtractor) {
			Assert.notNull(valueExtractor, "Value extractor must not be null");
			this.valueExtractor = valueExtractor;
		}

		@Override
		public boolean test(@Nullable MergedAnnotation<A> annotation) {
			if (!this.hasLastValue) {
				this.hasLastValue = true;
				this.lastValue = this.valueExtractor.apply(annotation);
			}
			Object value = this.valueExtractor.apply(annotation);
			return ObjectUtils.equals(value, this.lastValue);

		}
	}

	/**
	 * {@link Predicate} implementation used for
	 * {@link MergedAnnotationPredicates#unique(Function)}.
	 */
	private static class UniquePredicate<A extends Annotation, K> implements Predicate<MergedAnnotation<A>> {

		private final Function<? super MergedAnnotation<A>, K> keyExtractor;

		private final Set<K> seen = new HashSet<>();

		UniquePredicate(Function<? super MergedAnnotation<A>, K> keyExtractor) {
			Assert.notNull(keyExtractor, "Key extractor must not be null");
			this.keyExtractor = keyExtractor;
		}

		@Override
		public boolean test(@Nullable MergedAnnotation<A> annotation) {
			K key = this.keyExtractor.apply(annotation);
			return this.seen.add(key);
		}
	}

}
