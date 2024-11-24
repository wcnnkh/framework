package io.basc.framework.core.convert.factory;

import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.ConverterNotFoundException;
import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.core.convert.TypeDescriptor;

@FunctionalInterface
public interface ReversibleConverterFactory<S, E extends Throwable>
		extends ReversibleConverter<S, Object, E>, ConverterFactory<S, E> {

	@Override
	default <T> Converter<S, T, E> getConverter(Class<? extends T> targetType) {
		return getReversibleConverter(targetType);
	}

	<T> ReversibleConverter<S, T, E> getReversibleConverter(Class<? extends T> requiredType);

	@SuppressWarnings("unchecked")
	@Override
	default S reverseConvert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		if (canDirectlyConvert(sourceType, targetType)) {
			return (S) source;
		}

		ReversibleConverter<S, Object, E> converter = getReversibleConverter(sourceType.getType());
		if (converter == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return converter.reverseConvert(source, sourceType, targetType);
	}

	@Override
	default boolean canReverseConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		if (canDirectlyConvert(sourceType, targetType)) {
			return true;
		}

		ReversibleConverter<S, Object, E> converter = getReversibleConverter(sourceType.getType());
		if (converter == null) {
			return false;
		}
		return converter.canReverseConvert(sourceType, targetType);
	}
}
