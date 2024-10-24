package io.basc.framework.util.placeholder.support;

import io.basc.framework.util.placeholder.PlaceholderReplacer;
import io.basc.framework.util.placeholder.PlaceholderResolver;
import io.basc.framework.util.placeholder.PlaceholderFormat;

public class SimplePlaceholderFormat implements PlaceholderFormat {
	private final PlaceholderReplacer placeholderReplacer;
	private final PlaceholderResolver placeholderResolver;

	public SimplePlaceholderFormat(PlaceholderReplacer placeholderReplacer, PlaceholderResolver placeholderResolver) {
		this.placeholderReplacer = placeholderReplacer;
		this.placeholderResolver = placeholderResolver;
	}

	@Override
	public String replacePlaceholders(String source) {
		return placeholderReplacer.replacePlaceholders(source, placeholderResolver);
	}

	@Override
	public String replaceRequiredPlaceholders(String source) throws IllegalArgumentException {
		return placeholderReplacer.replaceRequiredPlaceholders(source, placeholderResolver);
	}
}
