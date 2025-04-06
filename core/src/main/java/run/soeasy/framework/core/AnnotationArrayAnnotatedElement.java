package run.soeasy.framework.core;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import lombok.ToString;

@ToString
public class AnnotationArrayAnnotatedElement implements AnnotatedElement, Serializable {
	private static final long serialVersionUID = 1L;
	protected final Annotation[] annotations;
	protected final Annotation[] declaredAnnotations;

	public AnnotationArrayAnnotatedElement(AnnotatedElement annotatedElement) {
		if (annotatedElement == null || annotatedElement instanceof EmptyAnnotatedElement) {
			this.annotations = AnnotationUtils.EMPTY;
			this.declaredAnnotations = AnnotationUtils.EMPTY;
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
		this.annotations = annotations == null ? AnnotationUtils.EMPTY : annotations;
		this.declaredAnnotations = declaredAnnotations == null ? AnnotationUtils.EMPTY : declaredAnnotations;
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return AnnotationUtils.getAnnotation(annotationClass, annotations);
	}

	@Override
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return AnnotationUtils.getAnnotation(annotationClass, declaredAnnotations);
	}

	public Annotation[] getAnnotations() {
		return annotations.clone();
	}

	public Annotation[] getDeclaredAnnotations() {
		return declaredAnnotations.clone();
	}

}
