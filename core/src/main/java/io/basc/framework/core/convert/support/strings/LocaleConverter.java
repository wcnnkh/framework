package io.basc.framework.core.convert.support.strings;

import java.util.Locale;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;

public class LocaleConverter implements ReversibleConverter<String, Locale, ConversionException> {

	@Override
	public Locale convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return StringUtils.isEmpty(source) ? null : Locale.forLanguageTag(source);
	}

	@Override
	public String reverseConvert(Locale source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.toLanguageTag();
	}

}
