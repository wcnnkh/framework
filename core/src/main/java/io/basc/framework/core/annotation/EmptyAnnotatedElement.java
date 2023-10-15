package io.basc.framework.core.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

class EmptyAnnotatedElement implements AnnotatedElement, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		return Annotations.EMPTY;
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return Annotations.EMPTY;
	}

}
