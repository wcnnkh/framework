package io.basc.framework.core.annotation;

import io.basc.framework.util.Wrapper;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotatedElementWrapper<A extends AnnotatedElement> extends Wrapper<A> implements AnnotatedElement {

	public AnnotatedElementWrapper(A wrappedTarget) {
		super(wrappedTarget);
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return wrappedTarget.getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getAnnotations() {
		return wrappedTarget.getAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return wrappedTarget.getDeclaredAnnotations();
	}

	@Override
	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		return wrappedTarget.getAnnotationsByType(annotationClass);
	}

	@Override
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return wrappedTarget.getDeclaredAnnotation(annotationClass);
	}

	@Override
	public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
		return wrappedTarget.getDeclaredAnnotationsByType(annotationClass);
	}
}
