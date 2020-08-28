package scw.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationArrayAnnotatedElement implements AnnotatedElement {
	private final Annotation[] annotations;

	public AnnotationArrayAnnotatedElement(Annotation[] annotations) {
		this.annotations = annotations == null ? AnnotatedElementUtils.EMPTY_ANNOTATION_ARRAY
				: annotations;
	}

	@SuppressWarnings("unchecked")
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		for (Annotation ann : annotations) {
			if (ann.annotationType() == annotationClass) {
				return (T) ann;
			}
		}
		return null;
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public Annotation[] getDeclaredAnnotations() {
		return annotations;
	}
}
