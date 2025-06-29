package run.soeasy.framework.core.convert.strings;

import java.util.Locale;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class StringToLocaleConverter implements StringConverter<Locale> {
	public static StringToLocaleConverter DEFAULT = new StringToLocaleConverter();
	

	@Override
	public Locale from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return StringUtils.isEmpty(source) ? null : Locale.forLanguageTag(source);
	}

	@Override
	public String to(Locale source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return source == null ? null : source.toLanguageTag();
	}

}
