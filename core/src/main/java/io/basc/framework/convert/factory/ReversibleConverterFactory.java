package io.basc.framework.convert.factory;

import io.basc.framework.convert.ConverterNotFoundException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;

@FunctionalInterface
public interface ReversibleConverterFactory<S, E extends Throwable>
		extends ReversibleConverter<S, Object, E>, ConverterFactory<S, E> {

	@Override
	<T> ReversibleConverter<S, T, E> getConverter(Class<? extends T> requiredType);

	@Override
	default S reverseConvert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		ReversibleConverter<S, Object, E> converter = getConverter(sourceType.getType());
		if (converter == null) {
			throw new ConverterNotFoundException(sourceType, targetType);
		}
		return converter.reverseConvert(source, sourceType, targetType);
	}

	@Override
	default boolean canReverseConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return getConverter(sourceType.getType()) != null;
	}
}
