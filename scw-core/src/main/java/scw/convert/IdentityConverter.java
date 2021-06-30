package scw.convert;

public class IdentityConverter<T> implements Converter<T, T> {
	@Override
	public T convert(T o) {
		return o;
	}
}
