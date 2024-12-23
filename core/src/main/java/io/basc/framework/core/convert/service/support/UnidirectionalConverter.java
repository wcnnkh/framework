package io.basc.framework.core.convert.service.support;

import io.basc.framework.core.convert.ConversionFailedException;
import io.basc.framework.core.convert.Converter;
import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.core.convert.TypeDescriptor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UnidirectionalConverter<S, T, E extends Throwable> implements ReversibleConverter<S, T, E> {
	private final Converter<S, T, E> converter;

	@Override
	public boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return converter.canConvert(sourceType, targetType);
	}

	@Override
	public T convert(S source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		return converter.convert(source, sourceType, targetType);
	}

	@Override
	public boolean canReverseConvert(TypeDescriptor sourceType, TypeDescriptor targetType) {
		return false;
	}

	@Override
	public S reverseConvert(T source, TypeDescriptor sourceType, TypeDescriptor targetType) throws E {
		throw new ConversionFailedException(sourceType, targetType, targetType, null);
	}

}
