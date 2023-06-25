package io.basc.framework.convert.strings;

import java.util.function.ToIntFunction;

import io.basc.framework.util.StringUtils;

public class StringToInteger extends StringToNumber implements ToIntFunction<String> {
	public static final StringToInteger DEFAULT = new StringToInteger(false, 10, 0);

	private final int defaultValue;

	public StringToInteger(boolean unsigned, int radix, int defaultValue) {
		super(unsigned, radix);
		this.defaultValue = defaultValue;
	}

	public int getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Integer apply(String source) {
		String value = format(source);
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return isUnsigned() ? Integer.parseUnsignedInt(value, getRadix()) : Integer.valueOf(value, getRadix());
	}

	@Override
	public int applyAsInt(String source) {
		Integer value = apply(source);
		return value == null ? defaultValue : value.intValue();
	}
}
