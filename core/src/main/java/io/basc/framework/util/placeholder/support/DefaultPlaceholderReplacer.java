package io.basc.framework.util.placeholder.support;

import io.basc.framework.factory.Configurable;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.factory.ServiceLoaderFactory;
import io.basc.framework.util.placeholder.ConfigurablePlaceholderReplacer;
import io.basc.framework.util.placeholder.PlaceholderReplacer;
import io.basc.framework.util.placeholder.PlaceholderResolver;

import java.util.Iterator;

public class DefaultPlaceholderReplacer implements ConfigurablePlaceholderReplacer, Configurable {
	private static final String DEFAULT_PREFIX = "{";
	private static final String DEFAULT_SUFFIX = "}";
	private static final PlaceholderReplacer DEFAULT_SIMPLE_REPLACER = new SimplePlaceholderReplaer(DEFAULT_PREFIX,
			DEFAULT_SUFFIX, true);
	private static final PlaceholderReplacer DEFAULT_SMART_REPLACER = new SmartPlaceholderReplacer(DEFAULT_PREFIX,
			DEFAULT_SUFFIX, true);
	private final ConfigurableServices<PlaceholderReplacer> placeholderReplacers = new ConfigurableServices<>(
			PlaceholderReplacer.class);

	public void addPlaceholderReplacer(PlaceholderReplacer placeholderReplacer) {
		placeholderReplacers.addService(placeholderReplacer);
	}

	@Override
	public Iterator<PlaceholderReplacer> iterator() {
		return placeholderReplacers.iterator();
	}

	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String textToUse = value;
		for (PlaceholderReplacer replacer : this) {
			textToUse = replacer.replacePlaceholders(textToUse, placeholderResolver);
		}

		textToUse = SimplePlaceholderReplaer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = SmartPlaceholderReplacer.NON_STRICT_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = DEFAULT_SIMPLE_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		textToUse = DEFAULT_SMART_REPLACER.replacePlaceholders(textToUse, placeholderResolver);
		return textToUse;
	}

	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver) {
		String textToUse = value;
		for (PlaceholderReplacer replacer : this) {
			textToUse = replacer.replacePlaceholders(textToUse,
					new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		}

		textToUse = SimplePlaceholderReplaer.NON_STRICT_REPLACER.replacePlaceholders(textToUse,
				new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		textToUse = SmartPlaceholderReplacer.NON_STRICT_REPLACER.replacePlaceholders(textToUse,
				new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		textToUse = DEFAULT_SIMPLE_REPLACER.replacePlaceholders(textToUse,
				new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		textToUse = DEFAULT_SMART_REPLACER.replacePlaceholders(textToUse,
				new RequiredPlaceholderResolver(textToUse, placeholderResolver));
		return textToUse;
	}

	@Override
	public void configure(ServiceLoaderFactory serviceLoaderFactory) {
		placeholderReplacers.configure(serviceLoaderFactory);
	}
}
