package io.basc.framework.convert;

import java.util.Enumeration;
import java.util.function.Function;

public class ConvertibleEnumeration<T, E> implements Enumeration<E> {
	private Enumeration<? extends T> enumeration;
	private Function<T, E> converter;

	public ConvertibleEnumeration(Enumeration<? extends T> enumeration, Function<T, E> converter) {
		this.enumeration = enumeration;
		this.converter = converter;
	}

	public boolean hasMoreElements() {
		return enumeration.hasMoreElements();
	}

	public E nextElement() {
		T v = enumeration.nextElement();
		if (v == null) {
			return null;
		}

		return converter.apply(v);
	}

	public static Enumeration<String> convertToStringEnumeration(Enumeration<?> enumeration) {
		return new ConvertibleEnumeration<Object, String>(enumeration, (k) -> String.valueOf(k));
	}
}
