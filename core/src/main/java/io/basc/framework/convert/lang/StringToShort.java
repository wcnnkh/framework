package io.basc.framework.convert.lang;

import io.basc.framework.util.StringUtils;

public class StringToShort extends StringToNumber {
	public static final StringToShort DEFAULT = new StringToShort(10, (short) 0);

	private final short defaultValue;

	public StringToShort(int radix, short defaultValue) {
		super(false, radix);
		this.defaultValue = defaultValue;
	}

	public short getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Short apply(String source) {
		String value = format(source);
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return Short.valueOf(value, getRadix());
	}

	public short applyAsShort(String source) {
		Short value = apply(source);
		return value == null ? defaultValue : value.shortValue();
	}
}
