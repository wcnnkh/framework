package run.soeasy.framework.core.convert.strings;

import java.nio.charset.Charset;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class CharsetConverter implements ReversibleConverter<String, Charset> {

	@Override
	public Charset convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return StringUtils.isEmpty(source) ? null : Charset.forName(source);
	}

	@Override
	public String reverseConvert(Charset source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.name();
	}

}
