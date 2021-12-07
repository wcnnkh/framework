package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;

/**
 * Strategy interface used to select between two {@link MergedAnnotation}
 * instances.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/annotation/MergedAnnotationSelector.java
 * @param <A> the annotation type
 * @see MergedAnnotationSelectors
 */
@FunctionalInterface
public interface MergedAnnotationSelector<A extends Annotation> {

	/**
	 * Determine if the existing annotation is known to be the best candidate and
	 * any subsequent selections may be skipped.
	 * 
	 * @param annotation the annotation to check
	 * @return {@code true} if the annotation is known to be the best candidate
	 */
	default boolean isBestCandidate(MergedAnnotation<A> annotation) {
		return false;
	}

	/**
	 * Select the annotation that should be used.
	 * 
	 * @param existing  an existing annotation returned from an earlier result
	 * @param candidate a candidate annotation that may be better suited
	 * @return the most appropriate annotation from the {@code existing} or
	 *         {@code candidate}
	 */
	MergedAnnotation<A> select(MergedAnnotation<A> existing, MergedAnnotation<A> candidate);

}
