package run.soeasy.framework.core.convert.config;

import lombok.NonNull;
import run.soeasy.framework.core.convert.Converter;
import run.soeasy.framework.core.convert.ConverterNotFoundException;
import run.soeasy.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface ConverterFactory<S, E extends Throwable> extends Converter<S, Object, E> {
	@SuppressWarnings("unchecked")
	default <T> T convert(@NonNull S source, @NonNull Class<? extends T> requiredType) throws E {
		return (T) convert(source, TypeDescriptor.forObject(source), TypeDescriptor.valueOf(requiredType));
	}

	@Override
	default Object convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return source;
		}

		Converter<S, Object, E> converter = getConverter(targetType.getType());
		if (converter == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return converter.convert(source, sourceType, targetType);
	}

	@Override
	default boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (canDirectlyConvert(sourceType, targetType)) {
			return true;
		}

		Converter<S, Object, E> converter = getConverter(targetType.getType());
		if (converter == null) {
			return false;
		}
		return converter.canConvert(sourceType, targetType);
	}

	<T> Converter<S, T, E> getConverter(Class<? extends T> requiredType);
}
