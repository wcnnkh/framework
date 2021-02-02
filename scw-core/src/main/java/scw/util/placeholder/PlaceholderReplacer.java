package scw.util.placeholder;

public interface PlaceholderReplacer {
	/** Prefix for system property placeholders: "${" */
	public static final String PLACEHOLDER_PREFIX = "${";

	/** Suffix for system property placeholders: "}" */
	public static final String PLACEHOLDER_SUFFIX = "}";
	
	String replacePlaceholders(String value, PlaceholderResolver placeholderResolver);
}
