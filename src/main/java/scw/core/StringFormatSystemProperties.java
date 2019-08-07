package scw.core;

import scw.core.utils.SystemPropertyUtils;

public final class StringFormatSystemProperties extends StringFormat {
	private static final StringFormatSystemProperties INSTANCE = new StringFormatSystemProperties(
			"{", "}");
	private static final StringFormatSystemProperties EL = new StringFormatSystemProperties(
			"${", "}");

	public StringFormatSystemProperties(String prefix, String suffix) {
		super(prefix, suffix);
	}

	public String getProperty(final String key) {
		return SystemPropertyUtils.getProperty(key);
	}

	public static String formatText(String text) {
		return INSTANCE.format(text);
	}

	public static String formatEL(String text) {
		return EL.format(text);
	}
}
