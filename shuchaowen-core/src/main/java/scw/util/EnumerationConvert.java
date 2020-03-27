package scw.util;

import java.util.Enumeration;

import scw.core.Converter;

public class EnumerationConvert<T, E> implements Enumeration<E> {
	private Enumeration<? extends T> enumeration;
	private Converter<T, E> converter;

	public EnumerationConvert(Enumeration<? extends T> enumeration,
			Converter<T, E> converter) {
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

		try {
			return converter.convert(v);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Enumeration<String> convertToStringEnumeration(
			Enumeration<?> enumeration) {
		return new EnumerationConvert<Object, String>(enumeration,
				new Converter<Object, String>() {

					public String convert(Object k) throws Exception {
						return k == null ? null : k.toString();
					}
				});
	}
}
