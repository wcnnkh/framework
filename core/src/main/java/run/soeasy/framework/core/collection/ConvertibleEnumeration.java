package run.soeasy.framework.core.collection;

import java.util.Enumeration;
import java.util.function.Function;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class ConvertibleEnumeration<T, E> implements Enumeration<E> {
	@NonNull
	private final Enumeration<? extends T> enumeration;
	@NonNull
	private final Function<? super T, ? extends E> converter;

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
