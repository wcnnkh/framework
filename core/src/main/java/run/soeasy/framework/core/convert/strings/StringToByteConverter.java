package run.soeasy.framework.core.convert.strings;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class StringToByteConverter implements StringConverter<Byte> {
	public static StringToByteConverter DEFAULT = new StringToByteConverter();

	private int radix = 10;

	@Override
	public Byte from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		if (StringUtils.isEmpty(source)) {
			return null;
		}
		return Byte.parseByte(source, radix);
	}

	@Override
	public String to(Byte source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return source == null ? null : Byte.toString(source);
	}

}
