package io.basc.framework.util.placeholder.support;

import io.basc.framework.util.placeholder.PlaceholderReplacer;
import io.basc.framework.util.placeholder.PlaceholderResolver;
import io.basc.framework.util.placeholder.PropertyResolver;

public class TempPropertyResolver implements PropertyResolver {
	private final PlaceholderReplacer placeholderReplacer;
	private final PlaceholderResolver placeholderResolver;

	public TempPropertyResolver(PlaceholderReplacer placeholderReplacer, PlaceholderResolver placeholderResolver) {
		this.placeholderReplacer = placeholderReplacer;
		this.placeholderResolver = placeholderResolver;
	}

	public String resolvePlaceholders(String text) {
		return placeholderReplacer.replacePlaceholders(text, placeholderResolver);
	}

	public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
		return placeholderReplacer.replacePlaceholders(text,
				new RequiredPlaceholderResolver(text, placeholderResolver));
	}

}
