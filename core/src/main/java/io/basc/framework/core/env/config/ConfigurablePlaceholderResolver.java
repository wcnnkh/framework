package io.basc.framework.core.env.config;

import io.basc.framework.util.placeholder.DefaultPlaceholderReplacer;
import io.basc.framework.util.placeholder.PlaceholderReplacer;
import io.basc.framework.util.placeholder.PlaceholderResolver;

public class ConfigurablePlaceholderResolver extends DefaultPlaceholderReplacer {
	private volatile PlaceholderReplacer parentPlaceholderReplacer;

	@Override
	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String text = super.replacePlaceholders(value, placeholderResolver);
		return parentPlaceholderReplacer == null ? text
				: parentPlaceholderReplacer.replacePlaceholders(text, placeholderResolver);
	}

	@Override
	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String text = super.replaceRequiredPlaceholders(value, placeholderResolver);
		return parentPlaceholderReplacer == null ? text
				: parentPlaceholderReplacer.replaceRequiredPlaceholders(text, placeholderResolver);
	}
}
