package io.basc.framework.convert.strings;

import io.basc.framework.util.StringUtils;

public class StringToByte extends StringToNumber {
	public static final StringToByte DEFAULT = new StringToByte(10, (byte) 0);

	private final byte defaultValue;

	public StringToByte(int radix, byte defaultValue) {
		super(false, radix);
		this.defaultValue = defaultValue;
	}

	public byte getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Byte apply(String source) {
		String value = format(source);
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return Byte.valueOf(value, getRadix());
	}

	public byte applyAsByte(String source) {
		Byte value = apply(source);
		return value == null ? defaultValue : value.byteValue();
	}
}
