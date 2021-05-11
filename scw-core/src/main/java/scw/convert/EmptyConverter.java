package scw.convert;

public class EmptyConverter<T> implements Converter<T, T> {
	@Override
	public T convert(T o) {
		return o;
	}
}
