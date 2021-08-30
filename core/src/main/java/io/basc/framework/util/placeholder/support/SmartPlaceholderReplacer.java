package io.basc.framework.util.placeholder.support;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;
import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.placeholder.PlaceholderReplacer;
import io.basc.framework.util.placeholder.PlaceholderResolver;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SmartPlaceholderReplacer implements PlaceholderReplacer{
	private static final Logger logger = LoggerFactory.getLogger(SmartPlaceholderReplacer.class);
	/** Value separator for system property placeholders: ":" */
	private static final String VALUE_SEPARATOR = ":";
	
	private static final Map<String, String> wellKnownSimplePrefixes = new HashMap<String, String>(4);
	static {
		wellKnownSimplePrefixes.put("}", "{");
		wellKnownSimplePrefixes.put("]", "[");
		wellKnownSimplePrefixes.put(")", "(");
	}
	
	public static final PlaceholderReplacer STRICT_REPLACER = new SmartPlaceholderReplacer(PLACEHOLDER_PREFIX,
			PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, false);

	public static final PlaceholderReplacer NON_STRICT_REPLACER = new SmartPlaceholderReplacer(PLACEHOLDER_PREFIX,
			PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, true);
	
	private final String placeholderPrefix;

	private final String placeholderSuffix;

	private final String simplePrefix;

	private final String valueSeparator;

	private final boolean ignoreUnresolvablePlaceholders;
	
	/**
	 * Creates a new {@code PropertyPlaceholderHelper} that uses the supplied
	 * prefix and suffix. Unresolvable placeholders are ignored.
	 * 
	 * @param placeholderPrefix
	 *            the prefix that denotes the start of a placeholder.
	 * @param placeholderSuffix
	 *            the suffix that denotes the end of a placeholder.
	 */
	public SmartPlaceholderReplacer(String placeholderPrefix, String placeholderSuffix) {
		this(placeholderPrefix, placeholderSuffix, null, true);
	}
	
	public SmartPlaceholderReplacer(String placeholderPrefix, String placeholderSuffix, boolean ignoreUnresolvablePlaceholders) {
		this(placeholderPrefix, placeholderSuffix, VALUE_SEPARATOR, ignoreUnresolvablePlaceholders);
	}

	/**
	 * Creates a new {@code PropertyPlaceholderHelper} that uses the supplied
	 * prefix and suffix.
	 * 
	 * @param placeholderPrefix
	 *            the prefix that denotes the start of a placeholder
	 * @param placeholderSuffix
	 *            the suffix that denotes the end of a placeholder
	 * @param valueSeparator
	 *            the separating character between the placeholder variable and
	 *            the associated default value, if any
	 * @param ignoreUnresolvablePlaceholders
	 *            indicates whether unresolvable placeholders should be ignored
	 *            ({@code true}) or cause an exception ({@code false}).
	 */
	public SmartPlaceholderReplacer(String placeholderPrefix, String placeholderSuffix, String valueSeparator,
			boolean ignoreUnresolvablePlaceholders) {

		Assert.notNull(placeholderPrefix, "placeholderPrefix must not be null");
		Assert.notNull(placeholderSuffix, "placeholderSuffix must not be null");
		this.placeholderPrefix = placeholderPrefix;
		this.placeholderSuffix = placeholderSuffix;
		String simplePrefixForSuffix = wellKnownSimplePrefixes.get(this.placeholderSuffix);
		if (simplePrefixForSuffix != null && this.placeholderPrefix.endsWith(simplePrefixForSuffix)) {
			this.simplePrefix = simplePrefixForSuffix;
		} else {
			this.simplePrefix = this.placeholderPrefix;
		}
		this.valueSeparator = valueSeparator;
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}
	
	public String replacePlaceholders(String value,
			PlaceholderResolver placeholderResolver) {
		Assert.notNull(value, "Argument 'value' must not be null.");
		return parseStringValue(value, placeholderResolver, new HashSet<String>());
	}
	
	protected String parseStringValue(String strVal, PlaceholderResolver placeholderResolver,
			Set<String> visitedPlaceholders) {

		StringBuilder buf = new StringBuilder(strVal);

		int startIndex = strVal.indexOf(this.placeholderPrefix);
		while (startIndex != -1) {
			int endIndex = findPlaceholderEndIndex(buf, startIndex);
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex + this.placeholderPrefix.length(), endIndex);
				String originalPlaceholder = placeholder;
				if (!visitedPlaceholders.add(originalPlaceholder)) {
					throw new IllegalArgumentException(
							"Circular placeholder reference '" + originalPlaceholder + "' in property definitions");
				}
				// Recursive invocation, parsing placeholders contained in the
				// placeholder key.
				placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders);
				// Now obtain the value for the fully resolved key...
				String propVal = placeholderResolver.resolvePlaceholder(placeholder);
				if (propVal == null && this.valueSeparator != null) {
					int separatorIndex = placeholder.indexOf(this.valueSeparator);
					if (separatorIndex != -1) {
						String actualPlaceholder = placeholder.substring(0, separatorIndex);
						String defaultValue = placeholder.substring(separatorIndex + this.valueSeparator.length());
						propVal = placeholderResolver.resolvePlaceholder(actualPlaceholder);
						if (propVal == null) {
							propVal = defaultValue;
						}
					}
				}
				if (propVal != null) {
					// Recursive invocation, parsing placeholders contained in
					// the
					// previously resolved placeholder value.
					propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders);
					buf.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
					if (logger.isTraceEnabled()) {
						logger.trace("Resolved placeholder '" + placeholder + "'");
					}
					startIndex = buf.indexOf(this.placeholderPrefix, startIndex + propVal.length());
				} else if (this.ignoreUnresolvablePlaceholders) {
					// Proceed with unprocessed value.
					startIndex = buf.indexOf(this.placeholderPrefix, endIndex + this.placeholderSuffix.length());
				} else {
					throw new IllegalArgumentException("Could not resolve placeholder '" + placeholder + "'"
							+ " in string value \"" + strVal + "\"");
				}
				visitedPlaceholders.remove(originalPlaceholder);
			} else {
				startIndex = -1;
			}
		}

		return buf.toString();
	}

	private int findPlaceholderEndIndex(CharSequence buf, int startIndex) {
		int index = startIndex + this.placeholderPrefix.length();
		int withinNestedPlaceholder = 0;
		while (index < buf.length()) {
			if (StringUtils.substringMatch(buf, index, this.placeholderSuffix)) {
				if (withinNestedPlaceholder > 0) {
					withinNestedPlaceholder--;
					index = index + this.placeholderSuffix.length();
				} else {
					return index;
				}
			} else if (StringUtils.substringMatch(buf, index, this.simplePrefix)) {
				withinNestedPlaceholder++;
				index = index + this.simplePrefix.length();
			} else {
				index++;
			}
		}
		return -1;
	}
}
