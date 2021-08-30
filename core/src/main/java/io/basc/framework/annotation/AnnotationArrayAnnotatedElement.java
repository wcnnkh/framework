package io.basc.framework.annotation;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationArrayAnnotatedElement implements AnnotatedElement, Serializable{
	private static final long serialVersionUID = 1L;
	private final Annotation[] annotations;
	private final Annotation[] declaredAnnotations;

	public AnnotationArrayAnnotatedElement(AnnotatedElement annotatedElement) {
		if (annotatedElement instanceof AnnotationArrayAnnotatedElement) {
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
	
	@Override
	public final <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		return AnnotatedElement.super.getAnnotationsByType(annotationClass);
	}
	
	@Override
	public final <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return AnnotatedElement.super.getDeclaredAnnotation(annotationClass);
	}
	
	@Override
	public final <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
		return AnnotatedElement.super.getDeclaredAnnotationsByType(annotationClass);
	}

	public Annotation[] getAnnotations() {
		return annotations;
	}

	public Annotation[] getDeclaredAnnotations() {
		return declaredAnnotations;
	}
}
