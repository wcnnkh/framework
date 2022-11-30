package io.basc.framework.util;

import java.util.HashSet;
import java.util.function.Function;
import java.util.function.Predicate;

public class StringToBoolean extends HashSet<String> implements Function<String, Boolean>, Predicate<String> {
	private static final long serialVersionUID = 1L;

	public static final StringToBoolean DEFAULT = new StringToBoolean(false);

	static {
		DEFAULT.add("1");
		DEFAULT.add("y");
		DEFAULT.add("t");
		DEFAULT.add("ok");
		DEFAULT.add("yes");
		DEFAULT.add("true");
		DEFAULT.add("success");
		DEFAULT.add("successful");
	}

	private final boolean defaultValue;

	public StringToBoolean(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Boolean apply(String text) {
		if (text == null) {
			return null;
		}

		String trimText = text.trim();
		if (trimText.isEmpty()) {
			return null;
		}

		return contains(trimText) || contains(trimText.toLowerCase());
	}

	public boolean applyAsBoolean(String text) {
		Boolean value = apply(text);
		return value == null ? defaultValue : value.booleanValue();
	}

	@Override
	public boolean test(String text) {
		return applyAsBoolean(text);
	}
}
