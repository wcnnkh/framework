package io.basc.framework.util.placeholder;

import io.basc.framework.factory.ConfigurableServices;

public class ConfigurablePlaceholderReplacer extends ConfigurableServices<PlaceholderReplacer>
		implements PlaceholderReplacer {

	public ConfigurablePlaceholderReplacer() {
		super(PlaceholderReplacer.class);
	}

	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String textToUse = value;
		for (PlaceholderReplacer replacer : getServices()) {
			textToUse = replacer.replacePlaceholders(textToUse, placeholderResolver);
		}
		return textToUse;
	}

	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String textToUse = value;
		for (PlaceholderReplacer replacer : getServices()) {
			textToUse = replacer.replaceRequiredPlaceholders(textToUse, placeholderResolver);
		}
		return textToUse;
	}
}
