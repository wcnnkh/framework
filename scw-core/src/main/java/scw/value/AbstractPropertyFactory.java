package scw.value;

import scw.util.placeholder.support.DefaultPlaceholderReplacer;

public abstract class AbstractPropertyFactory extends
		DefaultPlaceholderReplacer implements PropertyFactory {

	public String resolvePlaceholders(String text) {
		return replacePlaceholders(text, this);
	}

	public String resolveRequiredPlaceholders(String text)
			throws IllegalArgumentException {
		return replaceRequiredPlaceholders(text, this);
	}
}
