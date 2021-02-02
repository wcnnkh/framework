package scw.util.placeholder.support;

import scw.util.placeholder.PlaceholderReplacer;
import scw.util.placeholder.PlaceholderResolver;
import scw.util.placeholder.PropertyResolver;

public class TempPropertyResolver implements PropertyResolver{
	private final PlaceholderReplacer placeholderReplacer;
	private final PlaceholderResolver placeholderResolver;
	
	public TempPropertyResolver(PlaceholderReplacer placeholderReplacer, PlaceholderResolver placeholderResolver){
		this.placeholderReplacer = placeholderReplacer;
		this.placeholderResolver = placeholderResolver;
	}
	
	public String resolvePlaceholders(String text) {
		return placeholderReplacer.replacePlaceholders(text, placeholderResolver);
	}

	public String resolveRequiredPlaceholders(String text)
			throws IllegalArgumentException {
		return placeholderReplacer.replacePlaceholders(text, new RequiredPlaceholderResolver(text, placeholderResolver));
	}

}
