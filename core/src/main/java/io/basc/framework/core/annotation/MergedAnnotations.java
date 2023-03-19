/*
 * Copyright 2002-2022 the original author or authors.
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
import java.lang.annotation.Inherited;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;

public interface MergedAnnotations extends Iterable<MergedAnnotation<Annotation>> {

	<A extends Annotation> boolean isPresent(Class<A> annotationType);

	boolean isPresent(String annotationType);

	<A extends Annotation> boolean isDirectlyPresent(Class<A> annotationType);

	boolean isDirectlyPresent(String annotationType);

	<A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType);

	<A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType,
			@Nullable Predicate<? super MergedAnnotation<A>> predicate);

	<A extends Annotation> MergedAnnotation<A> get(Class<A> annotationType,
			@Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector);

	<A extends Annotation> MergedAnnotation<A> get(String annotationType);

	<A extends Annotation> MergedAnnotation<A> get(String annotationType,
			@Nullable Predicate<? super MergedAnnotation<A>> predicate);

	<A extends Annotation> MergedAnnotation<A> get(String annotationType,
			@Nullable Predicate<? super MergedAnnotation<A>> predicate, @Nullable MergedAnnotationSelector<A> selector);

	<A extends Annotation> Stream<MergedAnnotation<A>> stream(Class<A> annotationType);

	<A extends Annotation> Stream<MergedAnnotation<A>> stream(String annotationType);

	/**
	 * Stream all annotations and meta-annotations contained in this collection.
	 * <p>
	 * The resulting stream is ordered first by the
	 * {@linkplain MergedAnnotation#getAggregateIndex() aggregate index} and then by
	 * the annotation distance (with the closest annotations first). This ordering
	 * means that, for most use-cases, the most suitable annotations appear earliest
	 * in the stream.
	 * 
	 * @return a stream of annotations
	 */
	Stream<MergedAnnotation<Annotation>> stream();

	/**
	 * Create a new {@link MergedAnnotations} instance containing all annotations
	 * and meta-annotations from the specified element.
	 * <p>
	 * The resulting instance will not include any inherited annotations. If you
	 * want to include those as well you should use
	 * {@link #from(AnnotatedElement, SearchStrategy)} with an appropriate
	 * {@link SearchStrategy}.
	 * 
	 * @param element the source element
	 * @return a {@code MergedAnnotations} instance containing the element's
	 *         annotations
	 */
	static MergedAnnotations from(AnnotatedElement element) {
		return from(element, SearchStrategy.DIRECT);
	}

	/**
	 * Create a new {@link MergedAnnotations} instance containing all annotations
	 * and meta-annotations from the specified element and, depending on the
	 * {@link SearchStrategy}, related inherited elements.
	 * 
	 * @param element        the source element
	 * @param searchStrategy the search strategy to use
	 * @return a {@code MergedAnnotations} instance containing the merged element
	 *         annotations
	 */
	static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy) {
		return from(element, searchStrategy, RepeatableContainers.standardRepeatables());
	}

	/**
	 * Create a new {@link MergedAnnotations} instance containing all annotations
	 * and meta-annotations from the specified element and, depending on the
	 * {@link SearchStrategy}, related inherited elements.
	 * 
	 * @param element              the source element
	 * @param searchStrategy       the search strategy to use
	 * @param repeatableContainers the repeatable containers that may be used by the
	 *                             element annotations or the meta-annotations
	 * @return a {@code MergedAnnotations} instance containing the merged element
	 *         annotations
	 */
	static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy,
			RepeatableContainers repeatableContainers) {

		return from(element, searchStrategy, repeatableContainers, AnnotationFilter.PLAIN);
	}

	/**
	 * Create a new {@link MergedAnnotations} instance containing all annotations
	 * and meta-annotations from the specified element and, depending on the
	 * {@link SearchStrategy}, related inherited elements.
	 * 
	 * @param element              the source element
	 * @param searchStrategy       the search strategy to use
	 * @param repeatableContainers the repeatable containers that may be used by the
	 *                             element annotations or the meta-annotations
	 * @param annotationFilter     an annotation filter used to restrict the
	 *                             annotations considered
	 * @return a {@code MergedAnnotations} instance containing the merged
	 *         annotations for the supplied element
	 */
	static MergedAnnotations from(AnnotatedElement element, SearchStrategy searchStrategy,
			RepeatableContainers repeatableContainers, AnnotationFilter annotationFilter) {

		Assert.notNull(repeatableContainers, "RepeatableContainers must not be null");
		Assert.notNull(annotationFilter, "AnnotationFilter must not be null");
		return TypeMappedAnnotations.from(element, searchStrategy, repeatableContainers, annotationFilter);
	}

	/**
	 * Create a new {@link MergedAnnotations} instance from the specified
	 * annotations.
	 * 
	 * @param annotations the annotations to include
	 * @return a {@code MergedAnnotations} instance containing the annotations
	 * @see #from(Object, Annotation...)
	 */
	static MergedAnnotations from(Annotation... annotations) {
		return from(annotations, annotations);
	}

	/**
	 * Create a new {@link MergedAnnotations} instance from the specified
	 * annotations.
	 * 
	 * @param source      the source for the annotations. This source is used only
	 *                    for information and logging. It does not need to
	 *                    <em>actually</em> contain the specified annotations, and
	 *                    it will not be searched.
	 * @param annotations the annotations to include
	 * @return a {@code MergedAnnotations} instance containing the annotations
	 * @see #from(Annotation...)
	 * @see #from(AnnotatedElement)
	 */
	static MergedAnnotations from(Object source, Annotation... annotations) {
		return from(source, annotations, RepeatableContainers.standardRepeatables());
	}

	/**
	 * Create a new {@link MergedAnnotations} instance from the specified
	 * annotations.
	 * 
	 * @param source               the source for the annotations. This source is
	 *                             used only for information and logging. It does
	 *                             not need to <em>actually</em> contain the
	 *                             specified annotations, and it will not be
	 *                             searched.
	 * @param annotations          the annotations to include
	 * @param repeatableContainers the repeatable containers that may be used by
	 *                             meta-annotations
	 * @return a {@code MergedAnnotations} instance containing the annotations
	 */
	static MergedAnnotations from(Object source, Annotation[] annotations, RepeatableContainers repeatableContainers) {
		return from(source, annotations, repeatableContainers, AnnotationFilter.PLAIN);
	}

	/**
	 * Create a new {@link MergedAnnotations} instance from the specified
	 * annotations.
	 * 
	 * @param source               the source for the annotations. This source is
	 *                             used only for information and logging. It does
	 *                             not need to <em>actually</em> contain the
	 *                             specified annotations, and it will not be
	 *                             searched.
	 * @param annotations          the annotations to include
	 * @param repeatableContainers the repeatable containers that may be used by
	 *                             meta-annotations
	 * @param annotationFilter     an annotation filter used to restrict the
	 *                             annotations considered
	 * @return a {@code MergedAnnotations} instance containing the annotations
	 */
	static MergedAnnotations from(Object source, Annotation[] annotations, RepeatableContainers repeatableContainers,
			AnnotationFilter annotationFilter) {

		Assert.notNull(repeatableContainers, "RepeatableContainers must not be null");
		Assert.notNull(annotationFilter, "AnnotationFilter must not be null");
		return TypeMappedAnnotations.from(source, annotations, repeatableContainers, annotationFilter);
	}

	/**
	 * Create a new {@link MergedAnnotations} instance from the specified collection
	 * of directly present annotations. This method allows a
	 * {@code MergedAnnotations} instance to be created from annotations that are
	 * not necessarily loaded using reflection. The provided annotations must all be
	 * {@link MergedAnnotation#isDirectlyPresent() directly present} and must have
	 * an {@link MergedAnnotation#getAggregateIndex() aggregate index} of {@code 0}.
	 * <p>
	 * The resulting {@code MergedAnnotations} instance will contain both the
	 * specified annotations and any meta-annotations that can be read using
	 * reflection.
	 * 
	 * @param annotations the annotations to include
	 * @return a {@code MergedAnnotations} instance containing the annotations
	 * @see MergedAnnotation#of(ClassLoader, Object, Class, java.util.Map)
	 */
	static MergedAnnotations of(Collection<MergedAnnotation<?>> annotations) {
		return MergedAnnotationsCollection.of(annotations);
	}

	/**
	 * Search strategies supported by
	 * {@link MergedAnnotations#from(AnnotatedElement, SearchStrategy)} and variants
	 * of that method.
	 *
	 * <p>
	 * Each strategy creates a different set of aggregates that will be combined to
	 * create the final {@link MergedAnnotations}.
	 */
	enum SearchStrategy {

		/**
		 * Find only directly declared annotations, without considering
		 * {@link Inherited @Inherited} annotations and without searching superclasses
		 * or implemented interfaces.
		 */
		DIRECT,

		/**
		 * Find all directly declared annotations as well as any
		 * {@link Inherited @Inherited} superclass annotations.
		 * <p>
		 * This strategy is only really useful when used with {@link Class} types since
		 * the {@link Inherited @Inherited} annotation is ignored for all other
		 * {@linkplain AnnotatedElement annotated elements}.
		 * <p>
		 * This strategy does not search implemented interfaces.
		 */
		INHERITED_ANNOTATIONS,

		/**
		 * Find all directly declared and superclass annotations.
		 * <p>
		 * This strategy is similar to {@link #INHERITED_ANNOTATIONS} except the
		 * annotations do not need to be meta-annotated with
		 * {@link Inherited @Inherited}.
		 * <p>
		 * This strategy does not search implemented interfaces.
		 */
		SUPERCLASS,

		/**
		 * Perform a full search of the entire type hierarchy, including superclasses
		 * and implemented interfaces.
		 * <p>
		 * Superclass annotations do not need to be meta-annotated with
		 * {@link Inherited @Inherited}.
		 */
		TYPE_HIERARCHY,

		/**
		 * Perform a full search of the entire type hierarchy on the source <em>and</em>
		 * any enclosing classes.
		 * <p>
		 * This strategy is similar to {@link #TYPE_HIERARCHY} except that
		 * {@linkplain Class#getEnclosingClass() enclosing classes} are also searched.
		 * <p>
		 * Superclass and enclosing class annotations do not need to be meta-annotated
		 * with {@link Inherited @Inherited}.
		 * <p>
		 * When searching a {@link Method} source, this strategy is identical to
		 * {@link #TYPE_HIERARCHY}.
		 * <p>
		 * <strong>WARNING:</strong> This strategy searches recursively for annotations
		 * on the enclosing class for any source type, regardless whether the source
		 * type is an <em>inner class</em>, a {@code static} nested class, or a nested
		 * interface. Thus, it may find more annotations than you would expect.
		 */
		TYPE_HIERARCHY_AND_ENCLOSING_CLASSES

	}

}
