package io.basc.framework.text.placeholder.support;

import io.basc.framework.text.placeholder.PlaceholderReplacer;
import io.basc.framework.text.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.text.placeholder.PlaceholderResolver;

public class HierarchicalPlaceholderReplacer extends ConfigurablePlaceholderReplacer {
	private PlaceholderReplacer parentPlaceholderReplacer = DefaultPlaceholderReplacer.getInstance();

	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String textToUse = super.replacePlaceholders(value, placeholderResolver);
		if (parentPlaceholderReplacer != null) {
			textToUse = parentPlaceholderReplacer.replacePlaceholders(textToUse, placeholderResolver);
		}
		return textToUse;
	}

	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String textToUse = super.replaceRequiredPlaceholders(value, placeholderResolver);
		if (parentPlaceholderReplacer != null) {
			textToUse = parentPlaceholderReplacer.replaceRequiredPlaceholders(textToUse, placeholderResolver);
		}
		return textToUse;
	}

	public PlaceholderReplacer getParentPlaceholderReplacer() {
		return parentPlaceholderReplacer;
	}

	public void setParentPlaceholderReplacer(PlaceholderReplacer parentPlaceholderReplacer) {
		this.parentPlaceholderReplacer = parentPlaceholderReplacer;
	}
}
