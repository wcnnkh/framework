package io.basc.framework.core.convert.strings;

import java.util.Currency;

import io.basc.framework.core.convert.ConversionException;
import io.basc.framework.core.convert.ReversibleConverter;
import io.basc.framework.core.convert.TypeDescriptor;
import io.basc.framework.util.StringUtils;

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
