package io.basc.framework.util.placeholder.support;

import io.basc.framework.util.placeholder.PlaceholderResolver;

public class RequiredPlaceholderResolver implements PlaceholderResolver {
	private final PlaceholderResolver placeholderResolver;
	private final String text;

	public RequiredPlaceholderResolver(String text,
			PlaceholderResolver placeholderResolver) {
		this.placeholderResolver = placeholderResolver;
		this.text = text;
	}

	public String resolvePlaceholder(String placeholderName) {
		String value = placeholderResolver.resolvePlaceholder(placeholderName);
		if (value == null) {
			throw new IllegalArgumentException(
					"Could not resolve placeholder '" + placeholderName + "'"
							+ " in string value \"" + text + "\"");
		}
		return value;
	}
}