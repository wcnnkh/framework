package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;

public interface ConverterFactory<S, E extends Throwable> extends TransformerFactory<S, E>, Converter<S, Object, E> {

	default boolean isConverterRegistred(Class<?> type) {
		return getConverter(type) != null;
	}

	@Nullable
	<T> Converter<S, T, E> getConverter(Class<? extends T> type);

	<T> void registerConverter(Class<T> type, Converter<S, ? extends T, ? extends E> converter);

	@Override
	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return source;
		}

		Converter<S, Object, E> converter = getConverter(targetType.getType());
		if (converter == null) {
			Object target = newInstance(targetType);
			if (target == null) {
				return null;
			}
			transform(source, sourceType, target, targetType);
			return target;
		}
		return converter.convert(source, sourceType, targetType);
	}
}
