package io.basc.framework.lang.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

import io.basc.framework.core.annotation.AnnotatedElementUtils;
import lombok.NonNull;

public final class AnnotationUtils {
	public static final Annotation[] EMPTY = new Annotation[0];

	public static final AnnotatedElement EMPTY_ANNOTATED_ELEMENT = new EmptyAnnotatedElement();

	/**
	 * 获取一个注解，后面覆盖前面
	 * 
	 * @param type
	 * @param annotatedElements
	 * @return
	 */
	public static <T extends Annotation> T getAnnotation(@NonNull Class<T> type,
			AnnotatedElement... annotatedElements) {
		T old = null;
		for (AnnotatedElement annotatedElement : annotatedElements) {
			T a = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, type);
			if (a != null) {
				old = a;
			}
		}
		return old;
	}
}
