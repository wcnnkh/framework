package run.soeasy.framework.core.collection;

import java.util.Enumeration;
import java.util.function.Function;

import run.soeasy.framework.core.Assert;

public class ConvertibleEnumeration<T, E> implements Enumeration<E> {
	private Enumeration<? extends T> enumeration;
	private Function<? super T, ? extends E> converter;

	public ConvertibleEnumeration(Enumeration<? extends T> enumeration, Function<? super T, ? extends E> converter) {
		Assert.requiredArgument(enumeration != null, "enumeration");
		Assert.requiredArgument(converter != null, "converter");
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
