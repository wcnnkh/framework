package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;

/**
 * Callback interface that can be used to filter specific annotation types.
 *
 * <p>
 * Note that the {@link MergedAnnotations} model (which this interface has been
 * designed for) always ignores lang annotations according to the {@link #PLAIN}
 * filter (for efficiency reasons). Any additional filters and even custom
 * filter implementations apply within this boundary and may only narrow further
 * from here.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/annotation/AnnotationFilter.java
 * @see MergedAnnotations
 */
@FunctionalInterface
public interface AnnotationFilter {

	/**
	 * {@link AnnotationFilter} that matches annotations in the {@code java.lang}
	 * packages and their subpackages.
	 * <p>
	 * This is the default filter in the {@link MergedAnnotations} model.
	 */
	AnnotationFilter PLAIN = packages("java.lang");

	/**
	 * {@link AnnotationFilter} that matches annotations in the {@code java} and
	 * {@code javax} packages and their subpackages.
	 */
	AnnotationFilter JAVA = packages("java", "javax");

	/**
	 * {@link AnnotationFilter} that always matches and can be used when no relevant
	 * annotation types are expected to be present at all.
	 */
	AnnotationFilter ALL = new AnnotationFilter() {
		@Override
		public boolean matches(Annotation annotation) {
			return true;
		}

		@Override
		public boolean matches(Class<?> type) {
			return true;
		}

		@Override
		public boolean matches(String typeName) {
			return true;
		}

		@Override
		public String toString() {
			return "All annotations filtered";
		}
	};

	/**
	 * Test if the given annotation matches the filter.
	 * 
	 * @param annotation the annotation to test
	 * @return {@code true} if the annotation matches
	 */
	default boolean matches(Annotation annotation) {
		return matches(annotation.annotationType());
	}

	/**
	 * Test if the given type matches the filter.
	 * 
	 * @param type the annotation type to test
	 * @return {@code true} if the annotation matches
	 */
	default boolean matches(Class<?> type) {
		return matches(type.getName());
	}

	/**
	 * Test if the given type name matches the filter.
	 * 
	 * @param typeName the fully qualified class name of the annotation type to test
	 * @return {@code true} if the annotation matches
	 */
	boolean matches(String typeName);

	/**
	 * Create a new {@link AnnotationFilter} that matches annotations in the
	 * specified packages.
	 * 
	 * @param packages the annotation packages that should match
	 * @return a new {@link AnnotationFilter} instance
	 */
	static AnnotationFilter packages(String... packages) {
		return new PackagesAnnotationFilter(packages);
	}

}
