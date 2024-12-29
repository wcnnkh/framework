package io.basc.framework.core.convert.config;

import io.basc.framework.core.convert.ConverterNotFoundException;
import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.core.convert.TypeDescriptor;

public class ReversibleConverters<S, T, E extends Throwable, R extends ReversibleConverter<S, T, ? extends E>>
		extends Converters<S, T, E, R> implements ReversibleConverter<S, T, E> {
	@Override
	public boolean canReverseConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (R converter : this) {
			if (converter.canReverseConvert(sourceType, targetType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public S reverseConvert(T source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		for (R converter : this) {
			if (converter.canReverseConvert(sourceType, targetType)) {
				return converter.reverseConvert(source, sourceType, targetType);
			}
		}
		throw new ConverterNotFoundException(sourceType, targetType);
	}
}
