package scw.convert.lang;

import java.util.Locale;

import scw.convert.Converter;
import scw.core.utils.StringUtils;
import scw.lang.Nullable;

public class StringToLocaleConverter implements Converter<String, Locale> {

	@Nullable
	public Locale convert(String source) {
		return StringUtils.parseLocale(source);
	}

}
