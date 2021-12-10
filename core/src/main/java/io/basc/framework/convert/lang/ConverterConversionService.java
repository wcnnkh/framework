package io.basc.framework.convert.lang;

import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;

import java.util.Collections;
import java.util.Set;

public class ConverterConversionService extends ConditionalConversionService {
	@SuppressWarnings("rawtypes")
	private final Converter converter;
	private final Set<ConvertiblePair> convertibleTypes;

	public <S, T> ConverterConversionService(Class<S> sourceType, Class<T> targetType, Converter<S, T> converter) {
		this.convertibleTypes = Collections.singleton(new ConvertiblePair(sourceType, targetType));
		this.converter = converter;
	}

	public Set<ConvertiblePair> getConvertibleTypes() {
		return convertibleTypes;
	}

	@SuppressWarnings("unchecked")
	public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
		return converter.convert(source);
	}

	@Override
	public String toString() {
		return "<" + String.valueOf(converter) + ">" + convertibleTypes.toString();
	}
}
