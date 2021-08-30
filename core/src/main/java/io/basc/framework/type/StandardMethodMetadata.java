package io.basc.framework.type;

import io.basc.framework.annotation.AnnotatedElementUtils;
import io.basc.framework.util.Assert;
import io.basc.framework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * {@link MethodMetadata} implementation that uses standard reflection
 * to introspect a given {@code Method}.
 */
public class StandardMethodMetadata implements MethodMetadata {

	private final Method introspectedMethod;

	private final boolean nestedAnnotationsAsMap;


	/**
	 * Create a new StandardMethodMetadata wrapper for the given Method.
	 * @param introspectedMethod the Method to introspect
	 */
	public StandardMethodMetadata(Method introspectedMethod) {
		this(introspectedMethod, false);
	}

	public StandardMethodMetadata(Method introspectedMethod, boolean nestedAnnotationsAsMap) {
		Assert.notNull(introspectedMethod, "Method must not be null");
		this.introspectedMethod = introspectedMethod;
		this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
	}


	/**
	 * Return the underlying Method.
	 */
	public final Method getIntrospectedMethod() {
		return this.introspectedMethod;
	}

	public String getMethodName() {
		return this.introspectedMethod.getName();
	}

	public String getDeclaringClassName() {
		return this.introspectedMethod.getDeclaringClass().getName();
	}

	public String getReturnTypeName() {
		return this.introspectedMethod.getReturnType().getName();
	}

	public boolean isAbstract() {
		return Modifier.isAbstract(this.introspectedMethod.getModifiers());
	}

	public boolean isStatic() {
		return Modifier.isStatic(this.introspectedMethod.getModifiers());
	}

	public boolean isFinal() {
		return Modifier.isFinal(this.introspectedMethod.getModifiers());
	}

	public boolean isOverridable() {
		return (!isStatic() && !isFinal() && !Modifier.isPrivate(this.introspectedMethod.getModifiers()));
	}

	public boolean isAnnotated(String annotationName) {
		return AnnotatedElementUtils.isAnnotated(this.introspectedMethod, annotationName);
	}

	public Map<String, Object> getAnnotationAttributes(String annotationName) {
		return getAnnotationAttributes(annotationName, false);
	}

	public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		return AnnotatedElementUtils.getMergedAnnotationAttributes(this.introspectedMethod,
				annotationName, classValuesAsString, this.nestedAnnotationsAsMap);
	}

	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
		return getAllAnnotationAttributes(annotationName, false);
	}

	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		return AnnotatedElementUtils.getAllAnnotationAttributes(this.introspectedMethod,
				annotationName, classValuesAsString, this.nestedAnnotationsAsMap);
	}

}
