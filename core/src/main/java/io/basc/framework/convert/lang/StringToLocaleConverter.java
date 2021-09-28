package io.basc.framework.convert.lang;

import java.util.Locale;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;

public class StringToLocaleConverter implements Converter<String, Locale> {

	@Nullable
	public Locale convert(String source) {
		return Locale.forLanguageTag(source);
	}

}
