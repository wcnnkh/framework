package scw.core;

import scw.core.utils.ConfigUtils;

public final class StringFormatSystemProperties extends StringFormat {
	public StringFormatSystemProperties(String prefix, String suffix) {
		super(prefix, suffix);
	}

	public String getValue(final String key) {
		return ConfigUtils.getSystemProperty(key);
	}
}
