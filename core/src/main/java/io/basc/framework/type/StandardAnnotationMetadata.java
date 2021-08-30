package io.basc.framework.type;

import io.basc.framework.annotation.AnnotatedElementUtils;
import io.basc.framework.util.MultiValueMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link AnnotationMetadata} implementation that uses standard reflection
 * to introspect a given {@link Class}.
 */
public class StandardAnnotationMetadata extends StandardClassMetadata implements AnnotationMetadata {

	private final Annotation[] annotations;

	private final boolean nestedAnnotationsAsMap;


	/**
	 * Create a new {@code StandardAnnotationMetadata} wrapper for the given Class.
	 * @param introspectedClass the Class to introspect
	 * @see #StandardAnnotationMetadata(Class, boolean)
	 */
	public StandardAnnotationMetadata(Class<?> introspectedClass) {
		this(introspectedClass, false);
	}

	public StandardAnnotationMetadata(Class<?> introspectedClass, boolean nestedAnnotationsAsMap) {
		super(introspectedClass);
		this.annotations = introspectedClass.getAnnotations();
		this.nestedAnnotationsAsMap = nestedAnnotationsAsMap;
	}


	public Set<String> getAnnotationTypes() {
		Set<String> types = new LinkedHashSet<String>();
		for (Annotation ann : this.annotations) {
			types.add(ann.annotationType().getName());
		}
		return types;
	}

	public Set<String> getMetaAnnotationTypes(String annotationName) {
		return (this.annotations.length > 0 ?
				AnnotatedElementUtils.getMetaAnnotationTypes(getIntrospectedClass(), annotationName) : null);
	}

	public boolean hasAnnotation(String annotationName) {
		for (Annotation ann : this.annotations) {
			if (ann.annotationType().getName().equals(annotationName)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasMetaAnnotation(String annotationName) {
		return (this.annotations.length > 0 &&
				AnnotatedElementUtils.hasMetaAnnotationTypes(getIntrospectedClass(), annotationName));
	}

	public boolean isAnnotated(String annotationName) {
		return (this.annotations.length > 0 &&
				AnnotatedElementUtils.isAnnotated(getIntrospectedClass(), annotationName));
	}

	public Map<String, Object> getAnnotationAttributes(String annotationName) {
		return getAnnotationAttributes(annotationName, false);
	}

	public Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		return (this.annotations.length > 0 ? AnnotatedElementUtils.getMergedAnnotationAttributes(
				getIntrospectedClass(), annotationName, classValuesAsString, this.nestedAnnotationsAsMap) : null);
	}

	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName) {
		return getAllAnnotationAttributes(annotationName, false);
	}

	public MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString) {
		return (this.annotations.length > 0 ? AnnotatedElementUtils.getAllAnnotationAttributes(
				getIntrospectedClass(), annotationName, classValuesAsString, this.nestedAnnotationsAsMap) : null);
	}

	public boolean hasAnnotatedMethods(String annotationName) {
		try {
			Method[] methods = getIntrospectedClass().getDeclaredMethods();
			for (Method method : methods) {
				if (!method.isBridge() && method.getAnnotations().length > 0 &&
						AnnotatedElementUtils.isAnnotated(method, annotationName)) {
					return true;
				}
			}
			return false;
		}
		catch (Throwable ex) {
			throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(), ex);
		}
	}

	public Set<MethodMetadata> getAnnotatedMethods(String annotationName) {
		try {
			Method[] methods = getIntrospectedClass().getDeclaredMethods();
			Set<MethodMetadata> annotatedMethods = new LinkedHashSet<MethodMetadata>(4);
			for (Method method : methods) {
				if (!method.isBridge() && method.getAnnotations().length > 0 &&
						AnnotatedElementUtils.isAnnotated(method, annotationName)) {
					annotatedMethods.add(new StandardMethodMetadata(method, this.nestedAnnotationsAsMap));
				}
			}
			return annotatedMethods;
		}
		catch (Throwable ex) {
			throw new IllegalStateException("Failed to introspect annotated methods on " + getIntrospectedClass(), ex);
		}
	}

}
