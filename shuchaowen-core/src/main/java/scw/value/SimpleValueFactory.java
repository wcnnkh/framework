package scw.value;

import scw.core.StringFormat;
import scw.logger.Logger;
import scw.logger.LoggerUtils;
import scw.util.FormatUtils;
import scw.util.PropertyPlaceholderHelper;

public class SimpleValueFactory extends DefaultValueFactory<String> {
	private static Logger logger = LoggerUtils.getLogger(SimpleValueFactory.class);
	
	/**
	 * Resolve {@code $ ...} placeholders in the given text, replacing them with
	 * corresponding system property values.
	 * 
	 * @param text
	 *            the String to resolve
	 * @return the resolved String
	 * @see #PLACEHOLDER_PREFIX
	 * @see #PLACEHOLDER_SUFFIX
	 * @throws IllegalArgumentException
	 *             if there is an unresolvable placeholder
	 */
	public String resolvePlaceholders(String text) {
		return resolvePlaceholders(text, false);
	}

	/**
	 * Resolve {@code $ ...} placeholders in the given text, replacing them with
	 * corresponding system property values. Unresolvable placeholders with no
	 * default value are ignored and passed through unchanged if the flag is set
	 * to {@code true}.
	 * 
	 * @param text
	 *            the String to resolve
	 * @param ignoreUnresolvablePlaceholders
	 *            whether unresolved placeholders are to be ignored
	 * @return the resolved String
	 * @see #PLACEHOLDER_PREFIX
	 * @see #PLACEHOLDER_SUFFIX
	 * @throws IllegalArgumentException
	 *             if there is an unresolvable placeholder and the
	 *             "ignoreUnresolvablePlaceholders" flag is {@code false}
	 */
	public String resolvePlaceholders(String text,
			boolean ignoreUnresolvablePlaceholders) {
		PropertyPlaceholderHelper helper = (ignoreUnresolvablePlaceholders ? PropertyPlaceholderHelper.nonStrictHelper
				: PropertyPlaceholderHelper.strictHelper);
		return helper.replacePlaceholders(text,
				new PropertyPlaceholderResolver(text));
	}

	/**
	 * PlaceholderResolver implementation that resolves against system
	 * properties and system environment variables.
	 */
	private final class PropertyPlaceholderResolver implements
			PropertyPlaceholderHelper.PlaceholderResolver {
		private final String text;

		public PropertyPlaceholderResolver(String text) {
			this.text = text;
		}

		public String resolvePlaceholder(String placeholderName) {
			try {
				return getString(placeholderName);
			} catch (Throwable ex) {
				logger.error("Could not resolve placeholder '"
						+ placeholderName + "' in [" + this.text
						+ "] as system property: " + ex);
				return null;
			}
		}
	}

	public String format(String text, boolean supportEL) {
		return FormatUtils.format(text, this, supportEL);
	}

	public String format(String text, String prefix, String suffix) {
		return StringFormat.format(text, prefix, suffix, this);
	}
}
