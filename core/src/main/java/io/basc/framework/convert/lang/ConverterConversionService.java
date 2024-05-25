package io.basc.framework.convert.lang;

import java.util.Collections;
import java.util.Set;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ConversionFailedException;
import io.basc.framework.convert.ConvertiblePair;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.convert.config.ConditionalConversionService;
import io.basc.framework.util.function.Processor;

public class ConverterConversionService implements ConditionalConversionService {
	@SuppressWarnings("rawtypes")
	private final Processor converter;
	private final Set<ConvertiblePair> convertibleTypes;

	public <S, T> ConverterConversionService(Class<S> sourceType, Class<T> targetType,
			Processor<S, ? extends T, ? extends Throwable> converter) {
		this.convertibleTypes = Collections.singleton(new ConvertiblePair(sourceType, targetType));
		this.converter = converter;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertibleTypes;
	}

	@SuppressWarnings("unchecked")
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		try {
			return converter.process(source);
		} catch (Throwable e) {
			if (e instanceof ConversionException) {
				throw (ConversionException) e;
			}
			throw new ConversionFailedException(sourceType, targetType, source, e);
		}
	}

	@Override
	public String toString() {
		return "<" + String.valueOf(converter) + ">" + convertibleTypes.toString();
	}
}
