package io.basc.framework.text.placeholder;

import java.util.Properties;

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
}
