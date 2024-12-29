package io.basc.framework.core.convert.support.strings;

import java.nio.charset.Charset;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;

public class CharsetConverter implements ReversibleConverter<String, Charset, ConversionException> {

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
