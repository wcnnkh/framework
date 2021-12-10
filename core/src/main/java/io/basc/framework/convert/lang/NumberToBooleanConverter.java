package io.basc.framework.convert.lang;

import io.basc.framework.convert.Converter;

public class NumberToBooleanConverter implements Converter<Number, Boolean> {
	public static final NumberToBooleanConverter DEFAULT = new NumberToBooleanConverter(null);

	private final Boolean defaultValue;

	public NumberToBooleanConverter(Boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Boolean getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Boolean convert(Number o) {
		return o == null ? defaultValue : o.longValue() == 1;
	}
}
