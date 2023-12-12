package io.basc.framework.env;

import io.basc.framework.observe.properties.ObservablePropertyFactory;
import io.basc.framework.text.placeholder.PlaceholderFormat;
import io.basc.framework.text.placeholder.PlaceholderReplacer;

public interface PropertyResolver extends ObservablePropertyFactory, PlaceholderFormat {
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
