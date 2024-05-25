package io.basc.framework.convert.strings;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;

@SuppressWarnings("rawtypes")
public class EnumConverter implements ReversibleConverter<String, Enum, ConversionException> {

	@SuppressWarnings({ "unchecked" })
	@Override
	public Enum<?> convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return StringUtils.isEmpty(source) ? null : Enum.valueOf((Class<? extends Enum>) targetType.getType(), source);
	}

	@Override
	public String reverseConvert(Enum source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return source == null ? null : source.name();
	}

}
