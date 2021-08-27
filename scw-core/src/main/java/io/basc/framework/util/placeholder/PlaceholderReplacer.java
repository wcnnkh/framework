package io.basc.framework.util.placeholder;

import io.basc.framework.util.placeholder.support.RequiredPlaceholderResolver;

/**
 * 占位符替换
 * 
 * @author shuchaowen
 *
 */
public interface PlaceholderReplacer {
	/** Prefix for system property placeholders: "${" */
	public static final String PLACEHOLDER_PREFIX = "${";

	/** Suffix for system property placeholders: "}" */
	public static final String PLACEHOLDER_SUFFIX = "}";

	String replacePlaceholders(String value, PlaceholderResolver placeholderResolver);

	default String replaceRequiredPlaceholders(String value, PlaceholderResolver placeholderResolver)
			throws IllegalArgumentException {
		return replacePlaceholders(value, new RequiredPlaceholderResolver(value, placeholderResolver));
	}
}
