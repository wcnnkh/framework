package io.basc.framework.util;

import java.util.function.Function;

public class StringToCharacter implements Function<String, Character> {
	public static final StringToCharacter DEFAULT = new StringToCharacter(0, (char) 0);

	private final int index;
	private final char defaultValue;

	public StringToCharacter(int index, char defaultValue) {
		this.index = index;
		this.defaultValue = defaultValue;
	}

	public int getIndex() {
		return index;
	}

	public char getDefaultValue() {
		return defaultValue;
	}

	@Override
	public Character apply(String source) {
		if (StringUtils.isEmpty(source)) {
			return null;
		}

		return source.charAt(index);
	}

	public char applyAsChar(String source) {
		Character value = apply(source);
		return value == null ? defaultValue : value.charValue();
	}
}
