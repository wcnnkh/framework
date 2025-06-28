package run.soeasy.framework.core.convert.strings;

import java.util.Currency;

import run.soeasy.framework.core.StringUtils;
import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.TypeDescriptor;

public class StringToCurrencyConverter implements StringConverter<Currency> {
	public static StringToCurrencyConverter DEFAULT = new StringToCurrencyConverter();

	@Override
	public Currency from(String source, TypeDescriptor sourceType, TypeDescriptor targetType) throws ConversionException {
		return StringUtils.isEmpty(source) ? null : Currency.getInstance(source);
	}

	@Override
	public String to(Currency source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.getCurrencyCode();
	}

}
