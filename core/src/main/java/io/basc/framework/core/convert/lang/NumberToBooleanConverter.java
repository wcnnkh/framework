package io.basc.framework.core.convert.lang;

import java.util.function.Function;

public class NumberToBooleanConverter implements Function<Number, Boolean> {
	public static final NumberToBooleanConverter DEFAULT = new NumberToBooleanConverter(null);

	private final Boolean defaultValue;

	public NumberToBooleanConverter(Boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public Boolean getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Boolean apply(Number o) {
		return o == null ? defaultValue : o.longValue() == 1;
	}
}
