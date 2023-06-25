package io.basc.framework.convert.strings;

import java.util.function.ToLongFunction;

import io.basc.framework.util.StringUtils;

public class StringToLong extends StringToNumber implements ToLongFunction<String> {
	public static final StringToLong DEFAULT = new StringToLong(false, 10, 0);

	private final long defaultValue;

	public StringToLong(boolean unsigned, int radix, long defaultValue) {
		super(unsigned, radix);
		this.defaultValue = defaultValue;
	}

	public long getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Long apply(String source) {
		String value = format(source);
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return isUnsigned() ? Long.parseUnsignedLong(value, getRadix()) : Long.valueOf(value, getRadix());
	}

	@Override
	public long applyAsLong(String source) {
		Long value = apply(source);
		return value == null ? defaultValue : value.longValue();
	}
}
