package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

import io.basc.framework.lang.Nullable;

/**
 * Strategy API for extracting a value for an annotation attribute from a given
 * source object which is typically an {@link Annotation}, {@link Map}, or
 * {@link TypeMappedAnnotation}.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/annotation/ValueExtractor.java
 */
@FunctionalInterface
interface ValueExtractor {

	/**
	 * Extract the annotation attribute represented by the supplied {@link Method}
	 * from the supplied source {@link Object}.
	 */
	@Nullable
	Object extract(Method attribute, @Nullable Object object);

}
