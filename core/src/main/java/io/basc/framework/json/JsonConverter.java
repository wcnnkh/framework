package io.basc.framework.json;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;

public interface JsonConverter extends Converter<Object, Object, ConversionException> {
	@Override
	Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws JsonException, ConversionException;
}
