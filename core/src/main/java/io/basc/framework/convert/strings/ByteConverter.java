package io.basc.framework.convert.strings;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;

public class ByteConverter implements ReversibleConverter<String, Byte, ConversionException> {
	private int radix = 10;

	@Override
	public Byte convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return targetType.isPrimitive() ? (byte) 0 : null;
		}
		return Byte.parseByte(source, radix);
	}

	@Override
	public String reverseConvert(Byte source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return source == null ? null : source.toString();
	}

}
