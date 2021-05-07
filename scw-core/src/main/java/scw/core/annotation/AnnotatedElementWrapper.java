package scw.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import scw.core.utils.ObjectUtils;

public class AnnotatedElementWrapper<A extends AnnotatedElement> implements AnnotatedElement {
	protected final A target;

	public AnnotatedElementWrapper(A target) {
		this.target = target;
	}

	public A getTarget() {
		return target;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return target.getAnnotation(annotationClass);
	}

	@Override
	public Annotation[] getAnnotations() {
		return target.getAnnotations();
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return target.getDeclaredAnnotations();
	}

	@Override
	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		return target.getAnnotationsByType(annotationClass);
	}

	@Override
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return target.getDeclaredAnnotation(annotationClass);
	}

	@Override
	public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
		return target.getDeclaredAnnotationsByType(annotationClass);
	}

	@Override
	public String toString() {
		return target.toString();
	}

	@Override
	public int hashCode() {
		return target.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof AnnotatedElementWrapper) {
			return ObjectUtils.nullSafeEquals(((AnnotatedElementWrapper<?>) obj).target, this.target);
		}

		return ObjectUtils.nullSafeEquals(obj, this.target);
	}
}
