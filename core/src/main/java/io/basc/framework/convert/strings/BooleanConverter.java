package io.basc.framework.convert.strings;

import java.util.HashSet;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;

public class BooleanConverter extends HashSet<String>
		implements ReversibleConverter<String, Boolean, ConversionException> {
	private static final long serialVersionUID = 1L;

	public Boolean convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		Boolean value = StringUtils.isEmpty(source) ? null : Boolean.parseBoolean(source);
		if (value == null && targetType.isPrimitive()) {
			return false;
		}
		return value;
	}

	@Override
	public String reverseConvert(Boolean source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : Boolean.toString(source);
	}
}
