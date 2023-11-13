package io.basc.framework.text.placeholder.support;

import io.basc.framework.text.placeholder.PlaceholderReplacer;
import io.basc.framework.text.placeholder.ConfigurablePlaceholderReplacer;

public class DefaultPlaceholderReplacer extends ConfigurablePlaceholderReplacer {
	private static final String DEFAULT_PREFIX = "{";
	private static final String DEFAULT_SUFFIX = "}";
	private static final PlaceholderReplacer DEFAULT_SIMPLE_REPLACER = new SimplePlaceholderReplaer(DEFAULT_PREFIX,
			DEFAULT_SUFFIX, true);
	private static final PlaceholderReplacer DEFAULT_SMART_REPLACER = new SmartPlaceholderReplacer(DEFAULT_PREFIX,
			DEFAULT_SUFFIX, true);

	private static DefaultPlaceholderReplacer instance;

	public static DefaultPlaceholderReplacer getInstance() {
		if (instance == null) {
			synchronized (DefaultPlaceholderReplacer.class) {
				if (instance == null) {
					instance = new DefaultPlaceholderReplacer();
				}
			}
		}
		return instance;
	}

	public DefaultPlaceholderReplacer() {
		register(SimplePlaceholderReplaer.NON_STRICT_REPLACER);
		register(SmartPlaceholderReplacer.NON_STRICT_REPLACER);
		register(DEFAULT_SIMPLE_REPLACER);
		register(DEFAULT_SMART_REPLACER);
	}
}
