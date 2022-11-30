package io.basc.framework.util;

public class StringToFloat extends StringToNumber {
	public static final StringToFloat DEFAULT = new StringToFloat(0f);

	private final float defaultValue;

	public StringToFloat(float defaultValue) {
		super(false, 10);
		this.defaultValue = defaultValue;
	}

	public float getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Float apply(String source) {
		String value = format(source);
		if (StringUtils.isEmpty(value)) {
			return null;
		}

		return Float.valueOf(value);
	}

	public float applyAsFloat(String source) {
		Float value = apply(source);
		return value == null ? defaultValue : value.floatValue();
	}
}
