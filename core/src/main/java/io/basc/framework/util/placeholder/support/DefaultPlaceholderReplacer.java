package io.basc.framework.util.placeholder.support;

import io.basc.framework.util.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.util.placeholder.PlaceholderReplacer;
import io.basc.framework.util.placeholder.PlaceholderResolver;

public class DefaultPlaceholderReplacer extends ConfigurablePlaceholderReplacer {
	private static final String DEFAULT_PREFIX = "{";
	private static final String DEFAULT_SUFFIX = "}";
	private static final PlaceholderReplacer DEFAULT_SIMPLE_REPLACER = new SimplePlaceholderReplaer(DEFAULT_PREFIX,
			DEFAULT_SUFFIX, true);
	private static final PlaceholderReplacer DEFAULT_SMART_REPLACER = new SmartPlaceholderReplacer(DEFAULT_PREFIX,
			DEFAULT_SUFFIX, true);

	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String textToUse = super.replacePlaceholders(value, placeholderResolver);
		textToUse = SimplePlaceholderReplaer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = SmartPlaceholderReplacer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = DEFAULT_SIMPLE_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = DEFAULT_SMART_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		return textToUse;
	}

	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String textToUse = super.replaceRequiredPlaceholders(value, placeholderResolver);
		textToUse = SimplePlaceholderReplaer.STRICT_REPLACER.replaceRequiredPlaceholders(textToUse,
				placeholderResolver);
		textToUse = SmartPlaceholderReplacer.STRICT_REPLACER.replaceRequiredPlaceholders(textToUse,
				placeholderResolver);
		textToUse = DEFAULT_SIMPLE_REPLACER.replaceRequiredPlaceholders(textToUse, placeholderResolver);
		textToUse = DEFAULT_SMART_REPLACER.replaceRequiredPlaceholders(textToUse, placeholderResolver);
		return textToUse;
	}
}
