package io.basc.framework.env.properties;

import io.basc.framework.text.placeholder.PlaceholderFormat;
import io.basc.framework.text.placeholder.support.HierarchicalPlaceholderReplacer;
import io.basc.framework.value.DynamicPropertyFactory;

public interface EnvironmentProperties extends DynamicPropertyFactory, PlaceholderFormat {
	HierarchicalPlaceholderReplacer getPlaceholderReplacer();

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
