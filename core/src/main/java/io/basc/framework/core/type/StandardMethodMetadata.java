package io.basc.framework.core.type;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import io.basc.framework.core.annotation.MergedAnnotations;
import io.basc.framework.core.annotation.MergedAnnotations.SearchStrategy;
import io.basc.framework.core.annotation.RepeatableContainers;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.util.MultiValueMap;

/**
 * {@link MethodMetadata} implementation that uses standard reflection to
 * introspect a given {@code Method}.
 *
 * @author https://github.com/spring-projects/spring-framework/blob/main/spring-core/src/main/java/org/springframework/core/type/StandardMethodMetadata.java
 */
public class StandardMethodMetadata implements MethodMetadata {

	private final Method introspectedMethod;

	private final boolean nestedAnnotationsAsMap;

	private final MergedAnnotations mergedAnnotations;

	/**
	 * Create a new StandardMethodMetadata wrapper for the given Method.
	 * 
	 * @param introspectedMethod the Method to introspect
	 */
	public StandardMethodMetadata(Method introspectedMethod) {
		this(introspectedMethod, false);
	}

	/**
	 * Create a new StandardMethodMetadata wrapper for the given Method, providing
	 * the option to return any nested annotations or annotation arrays in the form
	 * of {@link io.basc.framework.core.annotation.AnnotationAttributes} instead
	 * of actual {@link java.lang.annotation.Annotation} instances.
	 * 
	 * @param introspectedMethod     the Method to introspect
	 * @param nestedAnnotationsAsMap return nested annotations and annotation arrays
	 *                               as
	 *                               {@link io.basc.framework.core.annotation.AnnotationAttributes}
	 *                               for compatibility with ASM-based
	 *                               {@link AnnotationMetadata} implementations
	 */
	public StandardMethodMetadata(Method introspectedMethod, boolean nestedAnnotationsAsMap) {
		Assert.notNull(introspectedMethod, "Method must not be null");
		this.introspectedMethod = introspectedMethod;
		this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
		this.mergedAnnotations = MergedAnnotations.from(introspectedMethod, SearchStrategy.DIRECT,
				RepeatableContainers.none());
	}

	@Override
	public MergedAnnotations getAnnotations() {
		return this.mergedAnnotations;
	}

	/**
	 * Return the underlying Method.
	 */
	public final Method getIntrospectedMethod() {
		return this.introspectedMethod;
	}

	@Override
	public String getMethodName() {
		return this.introspectedMethod.getName();
	}

	@Override
	public String getDeclaringClassName() {
		return this.introspectedMethod.getDeclaringClass().getName();
	}

	@Override
	public String getReturnTypeName() {
		return this.introspectedMethod.getReturnType().getName();
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(this.introspectedMethod.getModifiers());
	}

	@Override
	public boolean isStatic() {
		return Modifier.isStatic(this.introspectedMethod.getModifiers());
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(this.introspectedMethod.getModifiers());
	}

	@Override
	public boolean isOverridable() {
		return !isStatic() && !isFinal() && !isPrivate();
	}

	private boolean isPrivate() {
		return Modifier.isPrivate(this.introspectedMethod.getModifiers());
	}

	@Override
	@Nullable
	public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		if (this.nestedAnnotationsAsMap) {
			return MethodMetadata.super.getAnnotationAttributes(annotationName, classValuesAsString);
		}
		return AnnotatedElementUtils.getMergedAnnotationAttributes(this.introspectedMethod, annotationName,
				classValuesAsString, false);
	}

	@Override
	@Nullable
	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName,
			boolean classValuesAsString) {
		if (this.nestedAnnotationsAsMap) {
			return MethodMetadata.super.getAllAnnotationAttributes(annotationName, classValuesAsString);
		}
		return AnnotatedElementUtils.getAllAnnotationAttributes(this.introspectedMethod, annotationName,
				classValuesAsString, false);
	}

	@Override
	public boolean equals(@Nullable Object obj) {
		return ((this == obj) || ((obj instanceof StandardMethodMetadata)
				&& this.introspectedMethod.equals(((StandardMethodMetadata) obj).introspectedMethod)));
	}

	@Override
	public int hashCode() {
		return this.introspectedMethod.hashCode();
	}

	@Override
	public String toString() {
		return this.introspectedMethod.toString();
	}

}
