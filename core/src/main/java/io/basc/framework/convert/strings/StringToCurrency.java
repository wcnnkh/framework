package io.basc.framework.convert.strings;

import java.util.Currency;
import java.util.function.Function;

public class StringToCurrency implements Function<String, Currency> {
	public static final StringToCurrency DEFAULT = new StringToCurrency();

	public Currency apply(String source) {
		return Currency.getInstance(source);
	}

}
