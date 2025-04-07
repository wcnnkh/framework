package run.soeasy.framework.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import lombok.NonNull;
import run.soeasy.framework.util.collection.UnsafeArrayList;

public class AnnotatedElementUtils {
	public static final AnnotatedElement EMPTY_ANNOTATED_ELEMENT = new EmptyAnnotatedElement();

	public static <A extends AnnotatedElement, T extends Annotation> T getAnnotation(
			@NonNull Iterable<? extends A> annotatedElements, @NonNull Class<T> annotationClass,
			@NonNull Function<? super A, ? extends T> processor) {
		T annotation = null;
		List<T> list = null;
		for (A annotatedElement : annotatedElements) {
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

		if (list.size() == 1) {
			return list.get(0);
		}
		return SynthesizedAnnotation.synthesize(annotationClass, list);
	}

	public static <A extends AnnotatedElement, T> List<T> getAnnotations(
			@NonNull Iterable<? extends A> annotatedElements, @NonNull Function<? super A, ? extends T[]> processor) {
		T[] first = null;
		List<T> list = null;
		for (A annotatedElement : annotatedElements) {
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

	private AnnotatedElementUtils() {
	}
}
