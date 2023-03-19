package io.basc.framework.convert;

import io.basc.framework.lang.Nullable;

public interface Converter<S, T, E extends Throwable> extends ConverterConfiguration {

	default <R extends T> R convert(@Nullable S source, Class<? extends R> targetType) throws E {
		return convert(source, TypeDescriptor.forObject(source), targetType);
	}

	default <R extends T> R convert(@Nullable S source, @Nullable Class<? extends S> sourceType,
			Class<? extends R> targetType) throws E {
		return convert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	@SuppressWarnings("unchecked")
	default <R extends T> R convert(@Nullable S source, @Nullable Class<? extends S> sourceType,
			TypeDescriptor targetType) throws E {
		return (R) convert(source, TypeDescriptor.valueOf(sourceType), targetType);
	}

	default T convert(@Nullable S source, TypeDescriptor targetType) throws E {
		return convert(source, TypeDescriptor.forObject(source), targetType);
	}

	@SuppressWarnings("unchecked")
	default <R extends T> R convert(@Nullable S source, @Nullable TypeDescriptor sourceType,
			Class<? extends R> targetType) throws E {
		return (R) convert(source, sourceType, TypeDescriptor.valueOf(targetType));
	}

	T convert(@Nullable S source, @Nullable TypeDescriptor sourceType, TypeDescriptor targetType) throws E;
}
