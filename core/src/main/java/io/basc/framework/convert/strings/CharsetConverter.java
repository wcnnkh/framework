package io.basc.framework.convert.strings;

import java.nio.charset.Charset;

import io.basc.framework.convert.ConversionException;
import io.basc.framework.convert.ReversibleConverter;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;

public class CharsetConverter implements ReversibleConverter<String, Charset, ConversionException> {

	@Override
	public Charset convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return StringUtils.isEmpty(source) ? null : Charset.forName(source);
	}

	@Override
	public String invert(Charset source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.name();
	}

}
