package shuchaowen.common;

import shuchaowen.common.utils.ConfigUtils;

public final class StringFormatSystemProperties extends StringFormat{
	public StringFormatSystemProperties(String prefix, String suffix) {
		super(prefix, suffix);
	}

	@Override
	protected String getValue(final String key) {
		return ConfigUtils.getSystemProperty(key);
	}
}
