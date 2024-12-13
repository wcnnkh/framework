package io.basc.framework.util.placeholder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.basc.framework.util.Assert;
import io.basc.framework.util.StringUtils;
import io.basc.framework.util.logging.LogManager;
import io.basc.framework.util.logging.Logger;
import lombok.NonNull;

public class PropertyPlaceholderHelper implements PlaceholderReplacer {
	private static final Logger logger = LogManager.getLogger(PropertyPlaceholderHelper.class);
	/** Prefix for system property placeholders: "${" */
	public static final String PLACEHOLDER_PREFIX = "${";
	/** Suffix for system property placeholders: "}" */
	public static final String PLACEHOLDER_SUFFIX = "}";
	/** Value separator for system property placeholders: ":" */
	public static final String VALUE_SEPARATOR = ":";

	public static final PropertyPlaceholderHelper NON_STRICT_REPLACER = new PropertyPlaceholderHelper(
			PLACEHOLDER_PREFIX, PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, true);

	public static final PropertyPlaceholderHelper STRICT_REPLACER = new PropertyPlaceholderHelper(PLACEHOLDER_PREFIX,
			PLACEHOLDER_SUFFIX, VALUE_SEPARATOR, false);

	private static final Map<String, String> wellKnownSimplePrefixes = new HashMap<String, String>(4);

	static {
		wellKnownSimplePrefixes.put("}", "{");
		wellKnownSimplePrefixes.put("]", "[");
		wellKnownSimplePrefixes.put(")", "(");
	}

	private final boolean ignoreUnresolvablePlaceholders;

	private final String placeholderPrefix;

	private final String placeholderSuffix;

	private final String simplePrefix;

	private final String valueSeparator;

	/**
	 * Creates a new {@code PropertyPlaceholderHelper} that uses the supplied prefix
	 * and suffix. Unresolvable placeholders are ignored.
	 * 
	 * @param placeholderPrefix the prefix that denotes the start of a placeholder.
	 * @param placeholderSuffix the suffix that denotes the end of a placeholder.
	 */
	public PropertyPlaceholderHelper(String placeholderPrefix, String placeholderSuffix) {
		this(placeholderPrefix, placeholderSuffix, null, true);
	}

	/**
	 * Creates a new {@code PropertyPlaceholderHelper} that uses the supplied prefix
	 * and suffix.
	 * 
	 * @param placeholderPrefix              the prefix that denotes the start of a
	 *                                       placeholder
	 * @param placeholderSuffix              the suffix that denotes the end of a
	 *                                       placeholder
	 * @param valueSeparator                 the separating character between the
	 *                                       placeholder variable and the associated
	 *                                       default value, if any
	 * @param ignoreUnresolvablePlaceholders indicates whether unresolvable
	 *                                       placeholders should be ignored
	 *                                       ({@code true}) or cause an exception
	 *                                       ({@code false}).
	 */
	public PropertyPlaceholderHelper(@NonNull String placeholderPrefix, @NonNull String placeholderSuffix,
			String valueSeparator, boolean ignoreUnresolvablePlaceholders) {
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

	protected String parseStringValue(String strVal, PlaceholderResolver placeholderResolver,
			Set<String> visitedPlaceholders, boolean ignoreUnresolvablePlaceholders) {
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
				placeholder = parseStringValue(placeholder, placeholderResolver, visitedPlaceholders,
						ignoreUnresolvablePlaceholders);
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
					propVal = parseStringValue(propVal, placeholderResolver, visitedPlaceholders,
							ignoreUnresolvablePlaceholders);
					buf.replace(startIndex, endIndex + this.placeholderSuffix.length(), propVal);
					if (logger.isTraceEnabled()) {
						logger.trace("Resolved placeholder '" + placeholder + "'");
					}
					startIndex = buf.indexOf(this.placeholderPrefix, startIndex + propVal.length());
				} else if (ignoreUnresolvablePlaceholders) {
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

	public String replacePlaceholders(String value, PlaceholderResolver placeholderResolver) {
		Assert.notNull(value, "Argument 'value' must not be null.");
		return parseStringValue(value, placeholderResolver, new HashSet<String>(), this.ignoreUnresolvablePlaceholders);
	}

	@Override
	public String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver)
			throws IllegalArgumentException {
		Assert.notNull(value, "Argument 'value' must not be null.");
		return parseStringValue(value, placeholderResolver, new HashSet<String>(), false);
	}

	public PropertyPlaceholderHelper setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		return new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix, valueSeparator,
				ignoreUnresolvablePlaceholders);
	}

	public PropertyPlaceholderHelper setPlaceholderPrefix(@NonNull String placeholderPrefix) {
		return new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix, valueSeparator,
				ignoreUnresolvablePlaceholders);
	}

	public PropertyPlaceholderHelper setPlaceholderSuffix(@NonNull String placeholderSuffix) {
		return new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix, valueSeparator,
				ignoreUnresolvablePlaceholders);
	}

	public PropertyPlaceholderHelper setValueSeparator(String valueSeparator) {
		return new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix, valueSeparator,
				ignoreUnresolvablePlaceholders);
	}
}
