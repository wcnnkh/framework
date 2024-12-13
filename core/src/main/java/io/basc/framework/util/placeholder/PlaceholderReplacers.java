package io.basc.framework.util.placeholder;

import io.basc.framework.util.spi.ConfigurableServices;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlaceholderReplacers extends ConfigurableServices<PlaceholderReplacer> implements PlaceholderReplacer {

	public PlaceholderReplacers() {
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
