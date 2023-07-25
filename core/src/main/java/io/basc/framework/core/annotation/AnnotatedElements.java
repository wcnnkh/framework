package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.collect.UnsafeArrayList;

public abstract class AnnotatedElements implements AnnotatedElement {
	public static AnnotatedElement EMPTY = new EmptyAnnotatedElement();

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

	/**
	 * 注意其中的元素可能为空
	 * 
	 * @return 注解迭代器
	 */
	protected abstract Iterator<? extends AnnotatedElement> annotationElementIterator();

	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		return getAnnotation(annotationElementIterator(), annotationClass, (e) -> e.getAnnotation(annotationClass));
	}

	public Annotation[] getAnnotations() {
		return getAnnotations(annotationElementIterator(), (e) -> e.getAnnotations()).toArray(new Annotation[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> T[] getAnnotationsByType(Class<T> annotationClass) {
		return (T[]) getAnnotations(annotationElementIterator(), (e) -> e.getAnnotationsByType(annotationClass))
				.toArray();
	}

	@Override
	public <T extends Annotation> T getDeclaredAnnotation(Class<T> annotationClass) {
		return getAnnotation(annotationElementIterator(), annotationClass,
				(e) -> e.getDeclaredAnnotation(annotationClass));
	}

	public Annotation[] getDeclaredAnnotations() {
		return getAnnotations(annotationElementIterator(), (e) -> e.getDeclaredAnnotations())
				.toArray(new Annotation[0]);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends Annotation> T[] getDeclaredAnnotationsByType(Class<T> annotationClass) {
		return (T[]) getAnnotations(annotationElementIterator(), (e) -> e.getDeclaredAnnotationsByType(annotationClass))
				.toArray();
	}

	@Override
	public boolean isAnnotationPresent(Class<? extends Annotation> annotationClass) {
		Iterator<? extends AnnotatedElement> iterator = annotationElementIterator();
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
		return CollectionUtils.hashCode(annotationElementIterator());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof AnnotatedElements) {
			return CollectionUtils.equals(annotationElementIterator(),
					((AnnotatedElements) obj).annotationElementIterator());
		}
		return false;
	}
}
