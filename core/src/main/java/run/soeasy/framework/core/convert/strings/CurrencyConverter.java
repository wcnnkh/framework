package run.soeasy.framework.core.convert.strings;

import java.util.Currency;

import run.soeasy.framework.core.convert.ConversionException;
import run.soeasy.framework.core.convert.ReversibleConverter;
import run.soeasy.framework.core.convert.TypeDescriptor;
import run.soeasy.framework.util.StringUtils;

public class CurrencyConverter implements ReversibleConverter<String, Currency, ConversionException> {

	@Override
	public Currency convert(String source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return StringUtils.isEmpty(source) ? null : Currency.getInstance(source);
	}

	@Override
	public String reverseConvert(Currency source, TypeDescriptor sourceType, TypeDescriptor targetType)
			throws ConversionException {
		return source == null ? null : source.getCurrencyCode();
	}

}
