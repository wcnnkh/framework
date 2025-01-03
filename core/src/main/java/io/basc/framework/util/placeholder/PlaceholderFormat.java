package io.basc.framework.util.placeholder;

import java.util.Properties;
import java.util.Map.Entry;

import lombok.NonNull;

public interface PlaceholderFormat {
	/**
	 * Resolve ${...} placeholders in the given text, replacing them with
	 * corresponding property values as resolved by {@link Properties#getProperty}.
	 * Unresolvable placeholders with no default value are ignored and passed
	 * through unchanged.
	 * 
	 * @param source the String to resolve
	 * @return the resolved String (never {@code null})
	 * @throws IllegalArgumentException if given text is {@code null}
	 * @see #replaceRequiredPlaceholders(String)
	 */
	String replacePlaceholders(String source);

	/**
	 * Resolve ${...} placeholders in the given text, replacing them with
	 * corresponding property values as resolved by {@link Properties#getProperty}.
	 * Unresolvable placeholders with no default value will cause an
	 * IllegalArgumentException to be thrown.
	 * 
	 * @param source the String to resolve
	 * @return the resolved String (never {@code null})
	 * @throws IllegalArgumentException if given text is {@code null} or if any
	 *                                  placeholders are unresolvable
	 */
	String replaceRequiredPlaceholders(String source) throws IllegalArgumentException;

	default void formatProperties(@NonNull Properties sourceProperties, @NonNull Properties targetProperties) {
		for (Entry<Object, Object> entry : sourceProperties.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			if (value instanceof String) {
				targetProperties.put(entry.getKey(), replacePlaceholders((String) value));
			} else {
				targetProperties.put(entry.getKey(), entry.getValue());
			}
		}
	}
}
