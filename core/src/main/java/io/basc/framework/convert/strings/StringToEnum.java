package io.basc.framework.convert.strings;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.Converter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;

public class StringToEnum implements Converter<String, Enum<?>, ConversionException> {
	public static final StringToEnum DEFAULT = new StringToEnum();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Enum<?> convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return StringUtils.hasText(source) ? Enum.valueOf((Class<? extends Enum>) targetType.getType(), source) : null;
	}

}
