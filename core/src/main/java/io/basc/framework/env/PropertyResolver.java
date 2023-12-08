package io.basc.framework.env;

import io.basc.framework.text.placeholder.PlaceholderFormat;
import io.basc.framework.text.placeholder.PlaceholderReplacer;
import io.basc.framework.value.DynamicPropertyFactory;

public interface PropertyResolver extends DynamicPropertyFactory, PlaceholderFormat {
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
