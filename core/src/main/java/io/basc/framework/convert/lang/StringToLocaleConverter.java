package io.basc.framework.convert.lang;

import io.basc.framework.convert.Converter;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.StringUtils;

import java.util.Locale;

public class StringToLocaleConverter implements Converter<String, Locale> {

	@Nullable
	public Locale convert(String source) {
		return StringUtils.parseLocale(source);
	}

}
