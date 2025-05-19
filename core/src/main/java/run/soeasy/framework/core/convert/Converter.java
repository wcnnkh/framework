package run.soeasy.framework.core.convert;

import lombok.NonNull;

@FunctionalInterface
public interface Converter<S, T> {
	static class UnsupportedConverter<S, T> implements Converter<S, T> {
		static final UnsupportedConverter<?, ?> INSTANCE = new UnsupportedConverter<>();

		@Override
		public T convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
			throw new ConversionFailedException(sourceType, targetType, targetType, null);
		}
	}

	@SuppressWarnings("unchecked")
	public static <S, T> Converter<S, T> unsupported() {
		return (Converter<S, T>) UnsupportedConverter.INSTANCE;
	}

	T convert(S source, @NonNull TypeDescriptor sourceType, @NonNull TypeDescriptor targetType)
			throws ConversionException;
}