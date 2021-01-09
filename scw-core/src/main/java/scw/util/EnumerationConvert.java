package scw.util;

import java.util.Enumeration;

import scw.convert.Converter;

public class EnumerationConvert<T, E> implements Enumeration<E> {
	private Enumeration<? extends T> enumeration;
	private Converter<T, E> converter;

	public EnumerationConvert(Enumeration<? extends T> enumeration, Converter<T, E> converter) {
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

		return converter.convert(v);
	}

	public static Enumeration<String> convertToStringEnumeration(Enumeration<?> enumeration) {
		return new EnumerationConvert<Object, String>(enumeration, new Converter<Object, String>() {

			public String convert(Object k) {
				return String.valueOf(k);
			}
		});
	}
}
