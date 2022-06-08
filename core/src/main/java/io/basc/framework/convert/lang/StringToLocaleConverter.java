package io.basc.framework.convert.lang;

import java.util.Locale;
import java.util.function.Function;

import io.basc.framework.lang.Nullable;

public class StringToLocaleConverter implements Function<String, Locale> {

	@Nullable
	public Locale apply(String source) {
		return Locale.forLanguageTag(source);
	}

}
