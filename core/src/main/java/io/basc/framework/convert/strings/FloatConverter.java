package io.basc.framework.convert.strings;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;

public class FloatConverter implements ReversibleConverter<String, Float, ConversionException> {

	@Override
	public Float convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return targetType.isPrimitive() ? 0f : null;
		}
		return Float.parseFloat(source);
	}

	@Override
	public String reverseConvert(Float source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.toString();
	}

}
