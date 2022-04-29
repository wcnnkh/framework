package io.basc.framework.util.placeholder;

public interface PlaceholderFormat {
	/**
	 * Resolve ${...} placeholders in the given text, replacing them with
	 * corresponding property values as resolved by {@link #getProperty}.
	 * Unresolvable placeholders with no default value are ignored and passed
	 * through unchanged.
	 * 
	 * @param source
	 *            the String to resolve
	 * @return the resolved String (never {@code null})
	 * @throws IllegalArgumentException
	 *             if given text is {@code null}
	 * @see #resolveRequiredPlaceholders
	 */
	String formatPlaceholders(String source);

	/**
	 * Resolve ${...} placeholders in the given text, replacing them with
	 * corresponding property values as resolved by {@link #getProperty}.
	 * Unresolvable placeholders with no default value will cause an
	 * IllegalArgumentException to be thrown.
	 * 
	 * @param source
	 *            the String to resolve
	 * @return the resolved String (never {@code null})
	 * @throws IllegalArgumentException
	 *             if given text is {@code null} or if any placeholders are
	 *             unresolvable
	 */
	String formatRequiredPlaceholders(String source)
			throws IllegalArgumentException;
}
