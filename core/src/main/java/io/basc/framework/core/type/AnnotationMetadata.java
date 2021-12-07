package io.basc.framework.core.type;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

import io.basc.framework.core.annotation.MergedAnnotation;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.annotation.MergedAnnotations.SearchStrategy;

/**
 * Interface that defines abstract access to the annotations of a specific
 * class, in a form that does not require that class to be loaded yet.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/type/AnnotationMetadata.java
 * @see StandardAnnotationMetadata
 * @see io.basc.framework.core.type.classreading.MetadataReader#getAnnotationMetadata()
 * @see AnnotatedTypeMetadata
 */
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {

	/**
	 * Get the fully qualified class names of all annotation types that are
	 * <em>present</em> on the underlying class.
	 * 
	 * @return the annotation type names
	 */
	default Set<String> getAnnotationTypes() {
		return getAnnotations().stream().filter(MergedAnnotation::isDirectlyPresent)
				.map(annotation -> annotation.getType().getName()).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * Get the fully qualified class names of all meta-annotation types that are
	 * <em>present</em> on the given annotation type on the underlying class.
	 * 
	 * @param annotationName the fully qualified class name of the meta-annotation
	 *                       type to look for
	 * @return the meta-annotation type names, or an empty set if none found
	 */
	default Set<String> getMetaAnnotationTypes(String annotationName) {
		MergedAnnotation<?> annotation = getAnnotations().get(annotationName, MergedAnnotation::isDirectlyPresent);
		if (!annotation.isPresent()) {
			return Collections.emptySet();
		}
		return MergedAnnotations.from(annotation.getType(), SearchStrategy.INHERITED_ANNOTATIONS).stream()
				.map(mergedAnnotation -> mergedAnnotation.getType().getName())
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	/**
	 * Determine whether an annotation of the given type is <em>present</em> on the
	 * underlying class.
	 * 
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to look for
	 * @return {@code true} if a matching annotation is present
	 */
	default boolean hasAnnotation(String annotationName) {
		return getAnnotations().isDirectlyPresent(annotationName);
	}

	/**
	 * Determine whether the underlying class has an annotation that is itself
	 * annotated with the meta-annotation of the given type.
	 * 
	 * @param metaAnnotationName the fully qualified class name of the
	 *                           meta-annotation type to look for
	 * @return {@code true} if a matching meta-annotation is present
	 */
	default boolean hasMetaAnnotation(String metaAnnotationName) {
		return getAnnotations().get(metaAnnotationName, MergedAnnotation::isMetaPresent).isPresent();
	}

	/**
	 * Determine whether the underlying class has any methods that are annotated (or
	 * meta-annotated) with the given annotation type.
	 * 
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to look for
	 */
	default boolean hasAnnotatedMethods(String annotationName) {
		return !getAnnotatedMethods(annotationName).isEmpty();
	}

	/**
	 * Retrieve the method metadata for all methods that are annotated (or
	 * meta-annotated) with the given annotation type.
	 * <p>
	 * For any returned method, {@link MethodMetadata#isAnnotated} will return
	 * {@code true} for the given annotation type.
	 * 
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to look for
	 * @return a set of {@link MethodMetadata} for methods that have a matching
	 *         annotation. The return value will be an empty set if no methods match
	 *         the annotation type.
	 */
	Set<MethodMetadata> getAnnotatedMethods(String annotationName);

	/**
	 * Factory method to create a new {@link AnnotationMetadata} instance for the
	 * given class using standard reflection.
	 * 
	 * @param type the class to introspect
	 * @return a new {@link AnnotationMetadata} instance
	 */
	static AnnotationMetadata introspect(Class<?> type) {
		return StandardAnnotationMetadata.from(type);
	}

}
