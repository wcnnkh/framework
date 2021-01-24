package scw.util;

public class DefaultPropertyResolver implements PropertyResolver {
	private final PlaceholderResolver placeholderResolver;

	public DefaultPropertyResolver(PlaceholderResolver placeholderResolver) {
		this.placeholderResolver = placeholderResolver;
	}

	public PlaceholderResolver getPlaceholderResolver() {
		return placeholderResolver;
	}

	public String resolvePlaceholders(String text) {
		PlaceholderResolver placeholderResolver = getPlaceholderResolver();
		return PropertyPlaceholderHelper.nonStrictHelper.replacePlaceholders(
				StringFormat.format(text, placeholderResolver),
				placeholderResolver);
	}

	public String resolveRequiredPlaceholders(String text)
			throws IllegalArgumentException {
		PlaceholderResolver placeholderResolver = getPlaceholderResolver();
		return PropertyPlaceholderHelper.strictHelper.replacePlaceholders(
				StringFormat.format(text, placeholderResolver),
				placeholderResolver);
	}
}
