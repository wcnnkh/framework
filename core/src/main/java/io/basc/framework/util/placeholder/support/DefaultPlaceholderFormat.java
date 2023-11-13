package io.basc.framework.util.placeholder.support;

import io.basc.framework.text.placeholder.PlaceholderFormat;
import io.basc.framework.text.placeholder.PlaceholderResolver;
import io.basc.framework.text.placeholder.support.DefaultPlaceholderReplacer;

public class DefaultPlaceholderFormat extends DefaultPlaceholderReplacer implements PlaceholderFormat {
	private PlaceholderResolver placeholderResolver;

	public DefaultPlaceholderFormat(PlaceholderResolver placeholderResolver) {
		this.placeholderResolver = placeholderResolver;
	}

	@Override
	public String replacePlaceholders(String source) {
		return replacePlaceholders(source, placeholderResolver);
	}

	@Override
	public String replaceRequiredPlaceholders(String source) throws IllegalArgumentException {
		return replaceRequiredPlaceholders(source, placeholderResolver);
	}
}
