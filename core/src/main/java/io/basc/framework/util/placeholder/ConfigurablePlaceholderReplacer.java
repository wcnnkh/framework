package io.basc.framework.util.placeholder;

import io.basc.framework.util.spi.ConfigurableServices;

public class ConfigurablePlaceholderReplacer extends ConfigurableServices<PlaceholderReplacer>
		implements PlaceholderReplacer {
	public ConfigurablePlaceholderReplacer() {
		setServiceClass(PlaceholderReplacer.class);
	}

	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String textToUse = value;
		for (PlaceholderReplacer replacer : this) {
			textToUse = replacer.replacePlaceholders(textToUse, placeholderResolver);
		}
		return textToUse;
	}

	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String textToUse = value;
		for (PlaceholderReplacer replacer : this) {
			textToUse = replacer.replaceRequiredPlaceholders(textToUse, placeholderResolver);
		}
		return textToUse;
	}
}
