package run.soeasy.framework.core.convert.strings;

import java.util.Locale;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.core.strings.StringUtils;

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
