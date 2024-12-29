package io.basc.framework.core.mapping.config;

import io.basc.framework.core.convert.ConversionFailedException;
import io.basc.framework.core.convert.ConverterNotFoundException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.mapping.ReversibleMapper;

public class ReversibleMappers<S, T, E extends Throwable, M extends ReversibleMapper<S, T, ? extends E>>
		extends Mappers<S, T, E, M> implements ReversibleMapper<S, T, E> {
	@Override
	public boolean canReverseConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (M mapper : this) {
			if (mapper.canReverseConvert(sourceType, targetType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public S reverseConvert(T source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws E, ConversionFailedException {
		for (M mapper : this) {
			if (mapper.canReverseConvert(sourceType, targetType)) {
				return mapper.reverseConvert(source, sourceType, targetType);
			}
		}
		throw new ConverterNotFoundException(sourceType, targetType);
	}

	@Override
	public boolean canReverseTransform(TypeDescriptor sourceType, TypeDescriptor targetType) {
		for (M mapper : this) {
			if (mapper.canReverseTransform(sourceType, targetType)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void reverseTransform(T source, TypeDescriptor sourceType, S target, TypeDescriptor targetType) throws E {
		for (M mapper : this) {
			if (mapper.canReverseTransform(sourceType, targetType)) {
				mapper.reverseTransform(source, sourceType, target, targetType);
				return;
			}
		}
	}
}
