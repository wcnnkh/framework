package io.basc.framework.convert.strings;

import java.util.Locale;
import java.util.function.Function;

import io.basc.framework.lang.Nullable;

public class StringToLocale implements Function<String, Locale> {
	public static final StringToLocale DEFAULT = new StringToLocale();

	@Nullable
	public Locale apply(String source) {
		return Locale.forLanguageTag(source);
	}

}
