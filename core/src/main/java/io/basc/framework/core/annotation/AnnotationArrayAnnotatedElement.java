package io.basc.framework.core.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;

public class AnnotationArrayAnnotatedElement implements AnnotatedElement, Serializable {
	private static final long serialVersionUID = 1L;
	protected final Annotation[] annotations;
	protected final Annotation[] declaredAnnotations;

	public AnnotationArrayAnnotatedElement(AnnotatedElement annotatedElement) {
		if (annotatedElement == null || annotatedElement instanceof EmptyAnnotatedElement) {
			this.annotations = AnnotationUtils.EMPTY_ANNOTATION_ARRAY;
			this.declaredAnnotations = AnnotationUtils.EMPTY_ANNOTATION_ARRAY;
		} else if (annotatedElement instanceof AnnotationArrayAnnotatedElement) {
			this.annotations = ((AnnotationArrayAnnotatedElement) annotatedElement).annotations;
			this.declaredAnnotations = ((AnnotationArrayAnnotatedElement) annotatedElement).declaredAnnotations;
		} else {
			this.annotations = annotatedElement.getAnnotations();
			this.declaredAnnotations = annotatedElement.getDeclaredAnnotations();
		}
	}

	public AnnotationArrayAnnotatedElement(Annotation[] annotations) {
		this(annotations, annotations);
	}

	public AnnotationArrayAnnotatedElement(Annotation[] annotations, Annotation[] declaredAnnotations) {
		this.annotations = annotations == null ? AnnotationUtils.EMPTY_ANNOTATION_ARRAY : annotations;
		this.declaredAnnotations = declaredAnnotations == null ? AnnotationUtils.EMPTY_ANNOTATION_ARRAY
				: declaredAnnotations;
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
		return annotations.clone();
	}

	public Annotation[] getDeclaredAnnotations() {
		return declaredAnnotations.clone();
	}

	@Override
	public String toString() {
		return "AnnotationArrayAnnotatedElement(annotations=" + Arrays.toString(annotations) + ", declaredAnnotations="
				+ Arrays.toString(declaredAnnotations) + ")";
	}
}
