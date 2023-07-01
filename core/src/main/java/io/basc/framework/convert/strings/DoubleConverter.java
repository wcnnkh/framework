package io.basc.framework.convert.strings;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;

public class DoubleConverter implements ReversibleConverter<String, Double, ConversionException> {

	@Override
	public Double convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws NumberFormatException {
		if (StringUtils.isEmpty(source)) {
			return targetType.isPrimitive() ? (double) 0 : null;
		}
		return Double.parseDouble(source);
	}

	@Override
	public String invert(Double source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws NumberFormatException {
		return source == null ? null : source.toString();
	}

}
