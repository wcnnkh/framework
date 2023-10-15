package io.basc.framework.core.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.function.Supplier;

import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StringUtils;

public final class Annotations {
	public static final Annotation[] EMPTY = new Annotation[0];

	public static final AnnotatedElement EMPTY_ANNOTATED_ELEMENT = new EmptyAnnotatedElement();

	/**
	 * 获取一个注解，后面覆盖前面
	 * 
	 * @param type
	 * @param annotatedElements
	 * @return
	 */
	@Nullable
	public static <T extends Annotation> T getAnnotation(Class<T> type, AnnotatedElement... annotatedElements) {
		T old = null;
		for (AnnotatedElement annotatedElement : annotatedElements) {
			T a = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, type);
			if (a != null) {
				old = a;
			}
		}
		return old;
	}

	public static String getCharsetName(AnnotatedElement annotatedElement, Supplier<String> defaultSupplier) {
		CharsetName charsetName = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, CharsetName.class);
		if (charsetName == null || !StringUtils.hasText(charsetName.value())) {
			return defaultSupplier == null ? null : defaultSupplier.get();
		}
		return charsetName.value();
	}

	public static Boolean isNullable(AnnotatedElement annotatedElement) {
		Nullable nullable = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Nullable.class);
		if (nullable == null) {
			return false;
		}
		return nullable.value();
	}

	public static Boolean isNullable(AnnotatedElement annotatedElement, Supplier<Boolean> defaultSupplier) {
		Nullable nullable = AnnotatedElementUtils.getMergedAnnotation(annotatedElement, Nullable.class);
		if (nullable == null) {
			return defaultSupplier == null ? null : defaultSupplier.get();
		}
		return nullable.value();
	}
}
