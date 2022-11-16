package io.basc.framework.core.type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.annotation.AnnotationUtils;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.annotation.MergedAnnotations.SearchStrategy;
import io.basc.framework.core.annotation.RepeatableContainers;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.MultiValueMap;

/**
 * {@link AnnotationMetadata} implementation that uses standard reflection to
 * introspect a given {@link Class}.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/type/StandardAnnotationMetadata.java
 */
public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {

	private final MergedAnnotations mergedAnnotations;

	private final boolean nestedAnnotationsAsMap;

	@Nullable
	private Set<String> annotationTypes;

	/**
	 * Create a new {@code StandardAnnotationMetadata} wrapper for the given Class.
	 * 
	 * @param introspectedClass the Class to introspect
	 * @see #StandardAnnotationMetadata(Class, boolean)
	 */
	public StandardAnnotationMetadata(Class<?> introspectedClass) {
		this(introspectedClass, false);
	}

	/**
	 * Create a new {@link StandardAnnotationMetadata} wrapper for the given Class,
	 * providing the option to return any nested annotations or annotation arrays in
	 * the form of {@link io.basc.framework.core.annotation.AnnotationAttributes}
	 * instead of actual {@link Annotation} instances.
	 * 
	 * @param introspectedClass      the Class to introspect
	 * @param nestedAnnotationsAsMap return nested annotations and annotation arrays
	 *                               as
	 *                               {@link io.basc.framework.core.annotation.AnnotationAttributes}
	 *                               for compatibility with ASM-based
	 *                               {@link AnnotationMetadata} implementations
	 */
	public StandardAnnotationMetadata(Class<?> introspectedClass, boolean nestedAnnotationsAsMap) {
		super(introspectedClass);
		this.mergedAnnotations = MergedAnnotations.from(introspectedClass, SearchStrategy.INHERITED_ANNOTATIONS,
				RepeatableContainers.none());
		this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
	}

	@Override
	public MergedAnnotations getAnnotations() {
		return this.mergedAnnotations;
	}

	@Override
	public Set<String> getAnnotationTypes() {
		Set<String> annotationTypes = this.annotationTypes;
		if (annotationTypes == null) {
			annotationTypes = Collections.unmodifiableSet(AnnotationMetadata.super.getAnnotationTypes());
			this.annotationTypes = annotationTypes;
		}
		return annotationTypes;
	}

	@Override
	@Nullable
	public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		if (this.nestedAnnotationsAsMap) {
			return AnnotationMetadata.super.getAnnotationAttributes(annotationName, classValuesAsString);
		}
		return AnnotatedElementUtils.getMergedAnnotationAttributes(getIntrospectedClass(), annotationName,
				classValuesAsString, false);
	}

	@Override
	@Nullable
	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName,
			boolean classValuesAsString) {
		if (this.nestedAnnotationsAsMap) {
			return AnnotationMetadata.super.getAllAnnotationAttributes(annotationName, classValuesAsString);
		}
		return AnnotatedElementUtils.getAllAnnotationAttributes(getIntrospectedClass(), annotationName,
				classValuesAsString, false);
	}

	@Override
	public boolean hasAnnotatedMethods(String annotationName) {
		if (AnnotationUtils.isCandidateClass(getIntrospectedClass(), annotationName)) {
			try {
				return ReflectionUtils.getDeclaredMethods(getIntrospectedClass()).withInterfaces().all().stream()
						.filter((method) -> isAnnotatedMethod(method, annotationName)).findAny().isPresent();
			} catch (Throwable ex) {
				throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(),
						ex);
			}
		}
		return false;
	}

	@Override
	public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
		if (AnnotationUtils.isCandidateClass(getIntrospectedClass(), annotationName)) {
			try {
				return ReflectionUtils.getDeclaredMethods(getIntrospectedClass()).withInterfaces().all().stream()
						.filter((method) -> isAnnotatedMethod(method, annotationName))
						.map((method) -> new StandardMethodMetadata(method, this.nestedAnnotationsAsMap))
						.collect(Collectors.toCollection(() -> new LinkedHashSet<MethodMetadata>(4)));
			} catch (Throwable ex) {
				throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(),
						ex);
			}
		}
		return Collections.emptySet();
	}

	private static boolean isAnnotatedMethod(Method method, String annotationName) {
		return !method.isBridge() && method.getAnnotations().length > 0
				&& AnnotatedElementUtils.isAnnotated(method, annotationName);
	}

	static AnnotationMetadata from(Class<?> introspectedClass) {
		return new StandardAnnotationMetadata(introspectedClass, true);
	}

}
