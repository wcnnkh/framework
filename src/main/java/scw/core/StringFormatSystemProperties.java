package scw.core;

import scw.core.utils.SystemPropertyUtils;

public final class StringFormatSystemProperties extends StringFormat {
	public StringFormatSystemProperties(String prefix, String suffix) {
		super(prefix, suffix);
	}

	public String getValue(final String key) {
		return SystemPropertyUtils.getProperty(key);
	}
}
