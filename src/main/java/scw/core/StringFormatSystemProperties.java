package scw.core;

import scw.core.utils.SystemPropertyUtils;

public final class StringFormatSystemProperties extends StringFormat {
	private static final StringFormatSystemProperties INSTANCE = new StringFormatSystemProperties("{", "}");

	public StringFormatSystemProperties(String prefix, String suffix) {
		super(prefix, suffix);
	}

	public String getValue(final String key) {
		return SystemPropertyUtils.getProperty(key);
	}

	public static String formatText(String value) {
		return INSTANCE.format(value);
	}
}
