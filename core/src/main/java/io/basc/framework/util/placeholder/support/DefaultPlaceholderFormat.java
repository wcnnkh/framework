package io.basc.framework.util.placeholder.support;

import io.basc.framework.util.placeholder.ConfigurablePlaceholderFormat;
import io.basc.framework.util.placeholder.PlaceholderResolver;

public class DefaultPlaceholderFormat extends DefaultPlaceholderReplacer
		implements ConfigurablePlaceholderFormat {
	private PlaceholderResolver placeholderResolver;

	public DefaultPlaceholderFormat(PlaceholderResolver placeholderResolver) {
		this.placeholderResolver = placeholderResolver;
	}

	@Override
	public String formatPlaceholders(String source) {
		return replacePlaceholders(source, placeholderResolver);
	}

	@Override
	public String formatRequiredPlaceholders(String source)
			throws IllegalArgumentException {
		return replaceRequiredPlaceholders(source, placeholderResolver);
	}
}
