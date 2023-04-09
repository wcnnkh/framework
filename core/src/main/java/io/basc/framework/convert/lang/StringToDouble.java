package io.basc.framework.convert.lang;

import java.util.function.ToDoubleFunction;

import io.basc.framework.util.StringUtils;

public class StringToDouble extends StringToNumber implements ToDoubleFunction<String> {
	public static final StringToDouble DEFAULT = new StringToDouble(0d);

	private final double defaultValue;

	public StringToDouble(double defaultValue) {
		super(false, 10);
		this.defaultValue = defaultValue;
	}

	public double getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Double apply(String source) {
		String value = format(source);
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		return Double.valueOf(value);
	}

	@Override
	public double applyAsDouble(String source) {
		Double v = apply(source);
		return v == null ? defaultValue : v.doubleValue();
	}
}
