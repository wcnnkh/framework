package io.basc.framework.convert;

class UnsupportedConverter<S, T, E extends Throwable> implements Converter<S, T, E> {
	static final UnsupportedConverter<?, ?, ?> INSTANCE = new UnsupportedConverter<>();

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return false;
	}

	@Override
	public T convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		throw new ConversionFailedException(sourceType, targetType, targetType, null);
	}
}
