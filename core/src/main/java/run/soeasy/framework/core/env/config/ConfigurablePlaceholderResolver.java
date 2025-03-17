package run.soeasy.framework.core.env.config;

import run.soeasy.framework.util.placeholder.DefaultPlaceholderReplacer;
import run.soeasy.framework.util.placeholder.PlaceholderReplacer;
import run.soeasy.framework.util.placeholder.PlaceholderResolver;

public class ConfigurablePlaceholderResolver extends DefaultPlaceholderReplacer {
	private volatile PlaceholderReplacer parentPlaceholderReplacer;

	@Override
	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String text = super.replacePlaceholders(value, placeholderResolver);
		return parentPlaceholderReplacer == null ? text
				: parentPlaceholderReplacer.replacePlaceholders(text, placeholderResolver);
	}

	@Override
	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String text = super.replaceRequiredPlaceholders(value, placeholderResolver);
		return parentPlaceholderReplacer == null ? text
				: parentPlaceholderReplacer.replaceRequiredPlaceholders(text, placeholderResolver);
	}
}
