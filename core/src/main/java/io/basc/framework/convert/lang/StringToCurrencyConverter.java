package io.basc.framework.convert.lang;

import io.basc.framework.convert.Converter;

import java.util.Currency;

public class StringToCurrencyConverter implements Converter<String, Currency> {

	public Currency convert(String source) {
		return Currency.getInstance(source);
	}

}
