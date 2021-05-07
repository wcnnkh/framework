package scw.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class EmptyAnnotatedElement implements AnnotatedElement {

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return null;
	}

	@Override
	public Annotation[] getAnnotations() {
		return AnnotationUtils.EMPTY_ANNOTATION_ARRAY;
	}

	@Override
	public Annotation[] getDeclaredAnnotations() {
		return AnnotationUtils.EMPTY_ANNOTATION_ARRAY;
	}

}
