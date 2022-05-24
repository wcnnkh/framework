package io.basc.framework.convert.lang;

import java.util.Currency;
import java.util.function.Function;

public class StringToCurrencyConverter implements Function<String, Currency> {

	public Currency apply(String source) {
		return Currency.getInstance(source);
	}

}
