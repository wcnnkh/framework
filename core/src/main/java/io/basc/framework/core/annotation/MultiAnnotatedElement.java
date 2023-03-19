package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.UnsafeArrayList;

public class MultiAnnotatedElement implements AnnotatedElement {

	public static AnnotatedElement forAnnotatedElements(AnnotatedElement... annotatedElements) {
		return new MultiAnnotatedElement(annotatedElements);
	}

	public static <A extends AnnotatedElement, T extends Annotation> T getAnnotation(Iterator<? extends A> iterator,
			Class<? extends T> annotationClass, Function<? super A, ? extends T> processor) {
		T annotation = null;
		List<T> list = null;
		while (iterator.hasNext()) {
			A annotatedElement = iterator.next();
			if (annotatedElement == null) {
				continue;
			}

			T ann = processor.apply(annotatedElement);
			if (ann == null) {
				continue;
			}

			if (annotation == null) {
				annotation = ann;
			} else {
				if (list == null) {
					list = new ArrayList<>(2);
					list.add(annotation);
				}
				list.add(ann);
			}
		}

		if (list == null) {
			return annotation;
		}
		return MergedAnnotations.from(list.toArray(new Annotation[0])).get(annotationClass).synthesize();
	}

	public static <A extends AnnotatedElement, T> List<T> getAnnotations(Iterator<? extends A> iterator,
			Function<? super A, ? extends T[]> processor) {
		if (iterator == null || processor == null) {
			return Collections.emptyList();
		}

		T[] first = null;
		List<T> list = null;
		while (iterator.hasNext()) {
			A annotatedElement = iterator.next();
			if (annotatedElement == null) {
				continue;
			}

			T[] array = processor.apply(annotatedElement);
			if (array == null || array.length == 0) {
				continue;
			}

			if (first == null) {
				first = array;
			} else {
				if (list == null) {
					list = new ArrayList<>(4);
					list.addAll(new UnsafeArrayList<>(first));
				}
				list.addAll(new UnsafeArrayList<>(array));
			}
		}
		return list == null ? (first == null ? Collections.emptyList() : new UnsafeArrayList<>(first)) : list;
	}

	private final Iterable<? extends AnnotatedElement> annotatedElements;

	public MultiAnnotatedElement(AnnotatedElement... annotatedElements) {
		this.annotatedElements = annotatedElements == null ? Collections.emptyList() : Arrays.asList(annotatedElements);
	}

	public MultiAnnotatedElement(Iterable<? extends AnnotatedElement> annotatedElements) {
		this.annotatedElements = annotatedElements == null ? Collections.emptyList() : annotatedElements;
	}

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return getAnnotation(annotatedElements.iterator(), annotationClass, (e) -> e.getAnnotation(annotationClass));
	}

	public Annotation[] getAnnotations() {
		return getAnnotations(annotatedElements.iterator(), (e) -> e.getAnnotations()).toArray(new Annotation[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		return (T[]) getAnnotations(annotatedElements.iterator(), (e) -> e.getAnnotationsByType(annotationClass))
				.toArray();
	}

	@Override
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return getAnnotation(annotatedElements.iterator(), annotationClass,
				(e) -> e.getDeclaredAnnotation(annotationClass));
	}

	public Annotation[] getDeclaredAnnotations() {
		return getAnnotations(annotatedElements.iterator(), (e) -> e.getDeclaredAnnotations())
				.toArray(new Annotation[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
		return (T[]) getAnnotations(annotatedElements.iterator(),
				(e) -> e.getDeclaredAnnotationsByType(annotationClass)).toArray();
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		for (AnnotatedElement annotatedElement : annotatedElements) {
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

		if (obj instanceof MultiAnnotatedElement) {
			return CollectionUtils.equals(annotatedElements, ((MultiAnnotatedElement) obj).annotatedElements);
		}
		return false;
	}

	@Override
	public String toString() {
		return "MultiAnnotatedElement(annotatedElements=" + annotatedElements.toString() + ")";
	}
}