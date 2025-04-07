package run.soeasy.framework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.util.collection.CollectionUtils;

@RequiredArgsConstructor
@Getter
public class MergedAnnotatedElement implements AnnotatedElement {
	@NonNull
	private final Iterable<? extends AnnotatedElement> annotatedElements;

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return AnnotatedElementUtils.getAnnotation(annotatedElements, annotationClass,
				(e) -> e.getAnnotation(annotationClass));
	}

	public Annotation[] getAnnotations() {
		return AnnotatedElementUtils.getAnnotations(annotatedElements, (e) -> e.getAnnotations())
				.toArray(new Annotation[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		return (T[]) AnnotatedElementUtils
				.getAnnotations(annotatedElements, (e) -> e.getAnnotationsByType(annotationClass)).toArray();
	}

	@Override
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return AnnotatedElementUtils.getAnnotation(annotatedElements, annotationClass,
				(e) -> e.getDeclaredAnnotation(annotationClass));
	}

	public Annotation[] getDeclaredAnnotations() {
		return AnnotatedElementUtils.getAnnotations(annotatedElements, (e) -> e.getDeclaredAnnotations())
				.toArray(new Annotation[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
		return (T[]) AnnotatedElementUtils
				.getAnnotations(annotatedElements, (e) -> e.getDeclaredAnnotationsByType(annotationClass)).toArray();
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		Iterator<? extends AnnotatedElement> iterator = annotatedElements.iterator();
		if (iterator == null) {
			return false;
		}

		while (iterator.hasNext()) {
			AnnotatedElement annotatedElement = iterator.next();
			if (annotatedElement == null) {
				continue;
			}

			if (annotatedElement.isAnnotationPresent(annotationClass)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return CollectionUtils.hashCode(annotatedElements);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof MergedAnnotatedElement) {
			return CollectionUtils.equals(annotatedElements, ((MergedAnnotatedElement) obj).annotatedElements);
		}
		return false;
	}
}
