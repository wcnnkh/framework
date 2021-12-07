package io.basc.framework.core.type;

import java.lang.annotation.Annotation;
import java.util.Map;

import io.basc.framework.core.annotation.MergedAnnotation;
import io.basc.framework.core.annotation.MergedAnnotation.Adapt;
import io.basc.framework.core.annotation.MergedAnnotationCollectors;
import io.basc.framework.core.annotation.MergedAnnotationPredicates;
import io.basc.framework.core.annotation.MergedAnnotationSelectors;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.MultiValueMap;

/**
 * Defines access to the annotations of a specific type
 * ({@link AnnotationMetadata class} or {@link MethodMetadata method}), in a
 * form that does not necessarily require the class-loading.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/type/AnnotatedTypeMetadata.java
 * @see AnnotationMetadata
 * @see MethodMetadata
 */
public interface AnnotatedTypeMetadata {

	/**
	 * Return annotation details based on the direct annotations of the underlying
	 * element.
	 * 
	 * @return merged annotations based on the direct annotations
	 */
	MergedAnnotations getAnnotations();

	/**
	 * Determine whether the underlying element has an annotation or meta-annotation
	 * of the given type defined.
	 * <p>
	 * If this method returns {@code true}, then {@link #getAnnotationAttributes}
	 * will return a non-null Map.
	 * 
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to look for
	 * @return whether a matching annotation is defined
	 */
	default boolean isAnnotated(String annotationName) {
		return getAnnotations().isPresent(annotationName);
	}

	/**
	 * Retrieve the attributes of the annotation of the given type, if any (i.e. if
	 * defined on the underlying element, as direct annotation or meta-annotation),
	 * also taking attribute overrides on composed annotations into account.
	 * 
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to look for
	 * @return a Map of attributes, with the attribute name as key (e.g. "value")
	 *         and the defined attribute value as Map value. This return value will
	 *         be {@code null} if no matching annotation is defined.
	 */
	@Nullable
	default Map<String, Object> getAnnotationAttributes(String annotationName) {
		return getAnnotationAttributes(annotationName, false);
	}

	/**
	 * Retrieve the attributes of the annotation of the given type, if any (i.e. if
	 * defined on the underlying element, as direct annotation or meta-annotation),
	 * also taking attribute overrides on composed annotations into account.
	 * 
	 * @param annotationName      the fully qualified class name of the annotation
	 *                            type to look for
	 * @param classValuesAsString whether to convert class references to String
	 *                            class names for exposure as values in the returned
	 *                            Map, instead of Class references which might
	 *                            potentially have to be loaded first
	 * @return a Map of attributes, with the attribute name as key (e.g. "value")
	 *         and the defined attribute value as Map value. This return value will
	 *         be {@code null} if no matching annotation is defined.
	 */
	@Nullable
	default Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {

		MergedAnnotation<Annotation> annotation = getAnnotations().get(annotationName, null,
				MergedAnnotationSelectors.firstDirectlyDeclared());
		if (!annotation.isPresent()) {
			return null;
		}
		return annotation.asAnnotationAttributes(Adapt.values(classValuesAsString, true));
	}

	/**
	 * Retrieve all attributes of all annotations of the given type, if any (i.e. if
	 * defined on the underlying element, as direct annotation or meta-annotation).
	 * Note that this variant does <i>not</i> take attribute overrides into account.
	 * 
	 * @param annotationName the fully qualified class name of the annotation type
	 *                       to look for
	 * @return a MultiMap of attributes, with the attribute name as key (e.g.
	 *         "value") and a list of the defined attribute values as Map value.
	 *         This return value will be {@code null} if no matching annotation is
	 *         defined.
	 * @see #getAllAnnotationAttributes(String, boolean)
	 */
	@Nullable
	default MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
		return getAllAnnotationAttributes(annotationName, false);
	}

	/**
	 * Retrieve all attributes of all annotations of the given type, if any (i.e. if
	 * defined on the underlying element, as direct annotation or meta-annotation).
	 * Note that this variant does <i>not</i> take attribute overrides into account.
	 * 
	 * @param annotationName      the fully qualified class name of the annotation
	 *                            type to look for
	 * @param classValuesAsString whether to convert class references to String
	 * @return a MultiMap of attributes, with the attribute name as key (e.g.
	 *         "value") and a list of the defined attribute values as Map value.
	 *         This return value will be {@code null} if no matching annotation is
	 *         defined.
	 * @see #getAllAnnotationAttributes(String)
	 */
	@Nullable
	default MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName,
			boolean classValuesAsString) {

		Adapt[] adaptations = Adapt.values(classValuesAsString, true);
		return getAnnotations().stream(annotationName)
				.filter(MergedAnnotationPredicates.unique(MergedAnnotation::getMetaTypes))
				.map(MergedAnnotation::withNonMergedAttributes)
				.collect(MergedAnnotationCollectors.toMultiValueMap(map -> map.isEmpty() ? null : map, adaptations));
	}

}
