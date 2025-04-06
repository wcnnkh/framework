package run.soeasy.framework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import run.soeasy.framework.util.function.Wrapper;

@FunctionalInterface
public interface AnnotatedElementWrapper<W extends AnnotatedElement> extends AnnotatedElement, Wrapper<W> {
	@Override
	default <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return getSource().getAnnotation(annotationClass);
	}

	@Override
	default Annotation[] getDeclaredAnnotations() {
		return getSource().getDeclaredAnnotations();
	}

	@Override
	default Annotation[] getAnnotations() {
		return getSource().getAnnotations();
	}

	@Override
	default <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		return getSource().getAnnotationsByType(annotationClass);
	}

	@Override
	default <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return getSource().getDeclaredAnnotation(annotationClass);
	}

	@Override
	default <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
		return getSource().getDeclaredAnnotationsByType(annotationClass);
	}

	@Override
	default boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		return getSource().isAnnotationPresent(annotationClass);
	}
}
