package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class ByteConverter implements ReversibleConverter<String, Byte> {
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
	public String reverseConvert(Byte source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.toString();
	}

}
