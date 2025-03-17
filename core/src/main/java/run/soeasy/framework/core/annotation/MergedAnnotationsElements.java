/*
 * Copyright 2002-2020 the original author or authors.
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

package run.soeasy.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import lombok.Data;
import run.soeasy.framework.util.collections.Elements;

@Data
public final class MergedAnnotationsElements implements MergedAnnotations {
	private final Elements<? extends MergedAnnotations> elements;

	@Override
	public Iterator<MergedAnnotation<Annotation>> iterator() {
		return elements.flatMap((e) -> e).iterator();
	}

	@Override
	public <A extends Annotation> boolean isPresent(Class<A> annotationType) {
		return elements.anyMatch((e) -> e.isPresent(annotationType));
	}

	@Override
	public boolean isPresent(String annotationType) {
		return elements.anyMatch((e) -> e.isPresent(annotationType));
	}

	@Override
	public <A extends Annotation> boolean isDirectlyPresent(Class<A> annotationType) {
		return elements.anyMatch((e) -> e.isDirectlyPresent(annotationType));
	}

	@Override
	public boolean isDirectlyPresent(String annotationType) {
		return elements.anyMatch((e) -> e.isDirectlyPresent(annotationType));
	}

	private <A extends Annotation> MergedAnnotation<A> get(
			Function<? super MergedAnnotations, ? extends MergedAnnotation<A>> function) {
		List<MergedAnnotation<?>> list = new ArrayList<>();
		for (MergedAnnotations mergedAnnotations : elements) {
			MergedAnnotation<A> mergedAnnotation = function.apply(mergedAnnotations);
			if (mergedAnnotation == null) {
				continue;
			}
			list.add(mergedAnnotation);
		}

		MergedAnnotations mergedAnnotations = MergedAnnotationsCollection.of(list);
		return function.apply(mergedAnnotations);
	}

	@Override
	public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType) {
		return get((e) -> e.get(annotationType));
	}

	@Override
	public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType,
			Predicate<? super MergedAnnotation<A>> predicate) {
		return get((e) -> e.get(annotationType, predicate));
	}

	@Override
	public <A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType,
			Predicate<? super MergedAnnotation<A>> predicate, MergedAnnotationSelector<A> selector) {
		return get((e) -> e.get(annotationType, predicate, selector));
	}

	@Override
	public <A extends Annotation> MergedAnnotation<A> get(String annotationType) {
		return get((e) -> e.get(annotationType));
	}

	@Override
	public <A extends Annotation> MergedAnnotation<A> get(String annotationType,
			Predicate<? super MergedAnnotation<A>> predicate) {
		return get((e) -> e.get(annotationType, predicate));
	}

	@Override
	public <A extends Annotation> MergedAnnotation<A> get(String annotationType,
			Predicate<? super MergedAnnotation<A>> predicate, MergedAnnotationSelector<A> selector) {
		return get((e) -> e.get(annotationType, predicate, selector));
	}

	@Override
	public <A extends Annotation> Stream<MergedAnnotation<A>> stream(Class<A> annotationType) {
		return elements.flatMap((e) -> () -> e.stream(annotationType)).stream();
	}

	@Override
	public <A extends Annotation> Stream<MergedAnnotation<A>> stream(String annotationType) {
		Elements<MergedAnnotation<A>> elements = this.elements.flatMap((e) -> () -> e.stream(annotationType));
		return elements.stream();
	}

	@Override
	public Stream<MergedAnnotation<Annotation>> stream() {
		return elements.flatMap((e) -> () -> e.stream()).stream();
	}
}
