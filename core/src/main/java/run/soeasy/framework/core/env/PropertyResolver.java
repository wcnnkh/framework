package run.soeasy.framework.core.env;

import run.soeasy.framework.core.convert.transform.stereotype.Properties;
import run.soeasy.framework.util.placeholder.PlaceholderFormat;
import run.soeasy.framework.util.placeholder.PlaceholderReplacer;

public interface PropertyResolver extends Properties, PlaceholderFormat {
	PlaceholderReplacer getPlaceholderReplacer();

	/**
	 * 解析并替换文本
	 */
	@Override
	default String replacePlaceholders(String text) {
		return getPlaceholderReplacer().replacePlaceholders(text, (name) -> getAsString(name));
	}

	@Override
	default String replaceRequiredPlaceholders(String text) throws IllegalArgumentException {
		return getPlaceholderReplacer().replaceRequiredPlaceholders(text, (name) -> getAsString(name));
	}
}
