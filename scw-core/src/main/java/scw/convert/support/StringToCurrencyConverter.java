package scw.convert.support;

import java.util.Currency;

import scw.convert.Converter;

public class StringToCurrencyConverter implements Converter<String, Currency> {

	public Currency convert(String source) {
		return Currency.getInstance(source);
	}

}
