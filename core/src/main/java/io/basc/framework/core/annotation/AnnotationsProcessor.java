package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;

import io.basc.framework.lang.Nullable;

/**
 * Callback interface used to process annotations.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/annotation/AnnotationsProcessor.java
 * @param <C> the context type
 * @param <R> the result type
 * @see AnnotationsScanner
 * @see TypeMappedAnnotations
 */
@FunctionalInterface
interface AnnotationsProcessor<C, R> {

	/**
	 * Called when an aggregate is about to be processed. This method may return a
	 * {@code non-null} result to short-circuit any further processing.
	 * 
	 * @param context        the context information relevant to the processor
	 * @param aggregateIndex the aggregate index about to be processed
	 * @return a {@code non-null} result if no further processing is required
	 */
	@Nullable
	default R doWithAggregate(C context, int aggregateIndex) {
		return null;
	}

	/**
	 * Called when an array of annotations can be processed. This method may return
	 * a {@code non-null} result to short-circuit any further processing.
	 * 
	 * @param context        the context information relevant to the processor
	 * @param aggregateIndex the aggregate index of the provided annotations
	 * @param source         the original source of the annotations, if known
	 * @param annotations    the annotations to process (this array may contain
	 *                       {@code null} elements)
	 * @return a {@code non-null} result if no further processing is required
	 */
	@Nullable
	R doWithAnnotations(C context, int aggregateIndex, @Nullable Object source, Annotation[] annotations);

	/**
	 * Get the final result to be returned. By default this method returns the last
	 * process result.
	 * 
	 * @param result the last early exit result, or {@code null} if none
	 * @return the final result to be returned to the caller
	 */
	@Nullable
	default R finish(@Nullable R result) {
		return result;
	}

}
