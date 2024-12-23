package io.basc.framework.core.convert.lang;

import java.util.Collections;
import java.util.Set;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ConversionFailedException;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.core.convert.Value;
import io.basc.framework.core.convert.service.ConditionalConversionService;
import io.basc.framework.core.convert.service.ConvertiblePair;
import io.basc.framework.util.Pipeline;
import lombok.NonNull;

public class ConverterConversionService implements ConditionalConversionService {
	@SuppressWarnings("rawtypes")
	private final Pipeline converter;
	private final Set<ConvertiblePair> convertibleTypes;

	public <S, T> ConverterConversionService(Class<S> sourceType, Class<T> targetType,
			Pipeline<? super S, ? extends T, ? extends Throwable> converter) {
		this.convertibleTypes = Collections.singleton(new ConvertiblePair(sourceType, targetType));
		this.converter = converter;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertibleTypes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object convert(@NonNull Value value, @NonNull TypeDescriptor targetType) throws ConversionException {
		try {
			return converter.apply(value.get());
		} catch (Throwable e) {
			if (e instanceof ConversionException) {
				throw (ConversionException) e;
			}
			throw new ConversionFailedException(value.getTypeDescriptor(), targetType, value.get(), e);
		}
	}

	@Override
	public String toString() {
		return "<" + String.valueOf(converter) + ">" + convertibleTypes.toString();
	}
}
