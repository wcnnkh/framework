package scw.core;

import scw.core.utils.PropertyPlaceholderHelper.PlaceholderResolver;
import scw.core.utils.SystemPropertyUtils;

public final class SystemPropertyPlaceholderResolver implements PlaceholderResolver {

	private final String text;

	public SystemPropertyPlaceholderResolver(String text) {
		this.text = text;
	}

	public String resolvePlaceholder(String placeholderName) {
		try {
			return SystemPropertyUtils.getProperty(placeholderName);
		} catch (Throwable ex) {
			System.err.println("Could not resolve placeholder '" + placeholderName + "' in [" + this.text
					+ "] as system property: " + ex);
			return null;
		}
	}
}
