package scw.util;

public interface PlaceholderResolver {

	/**
	 * Resolves the supplied placeholder name into the replacement value.
	 * 
	 * @param placeholderName
	 *            the name of the placeholder to resolve
	 * @return the replacement value or {@code null} if no replacement is to
	 *         be made
	 */
	String resolvePlaceholder(String placeholderName);
}
