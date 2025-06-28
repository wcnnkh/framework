package run.soeasy.framework.core.convert.strings;

import java.nio.charset.Charset;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class StringToCharsetConverter implements StringConverter<Charset> {
	public static StringToCharsetConverter DEFAULT = new StringToCharsetConverter();

	@Override
	public Charset from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return StringUtils.isEmpty(source) ? null : Charset.forName(source);
	}

	@Override
	public String to(Charset source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.name();
	}

}
