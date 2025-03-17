package run.soeasy.framework.util.placeholder;

import lombok.Getter;
import lombok.Setter;
import run.soeasy.framework.util.spi.ConfigurableServices;

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
