package io.basc.framework.util.placeholder;

import java.util.Map;

/**
 * 占位符替换
 * 
 * @author wcnnkh
 *
 */
public interface PlaceholderReplacer {
	default String replacePlaceholders(String source, Map<String, ?> properties) {
		return replacePlaceholders(source, (key) -> {
			Object v = properties.get(key);
			return v == null ? null : v.toString();
		});
	}

	default String replaceRequiredPlaceholders(String source, Map<String, ?> properties)
			throws IllegalArgumentException {
		return replaceRequiredPlaceholders(source, (key) -> {
			Object v = properties.get(key);
			return v == null ? null : v.toString();
		});
	}

	String replacePlaceholders(String source, PlaceholderResolver placeholderResolver);

	String replaceRequiredPlaceholders(String source, PlaceholderResolver placeholderResolver)
			throws IllegalArgumentException;
}
