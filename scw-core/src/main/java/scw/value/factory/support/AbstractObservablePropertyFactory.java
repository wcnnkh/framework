package scw.value.factory.support;

import scw.util.placeholder.ConfigurablePropertyResolver;
import scw.util.placeholder.PlaceholderReplacer;
import scw.util.placeholder.PlaceholderResolver;
import scw.util.placeholder.support.DefaultPlaceholderResolver;
import scw.util.placeholder.support.DefaultPropertyResolver;
import scw.value.factory.ObservablePropertyFactory;

public abstract class AbstractObservablePropertyFactory extends AbstractConvertibleObservableValueFactory<String> implements ObservablePropertyFactory, ConfigurablePropertyResolver {
	private final DefaultPropertyResolver propertyResolver = new DefaultPropertyResolver(new DefaultPlaceholderResolver(this));
	
	public String resolvePlaceholders(String text) {
		return propertyResolver.resolvePlaceholders(text);
	}
	
	public String resolveRequiredPlaceholders(String text)
			throws IllegalArgumentException {
		return propertyResolver.resolveRequiredPlaceholders(text);
	}
	
	public void addPlaceholderReplacer(PlaceholderReplacer placeholderReplacer) {
		propertyResolver.addPlaceholderReplacer(placeholderReplacer);
	}
	
	public String replacePlaceholders(String value,
			PlaceholderResolver placeholderResolver) {
		return propertyResolver.replacePlaceholders(value, placeholderResolver);
	}
	
	public String replaceRequiredPlaceholders(String value,
			PlaceholderResolver placeholderResolver) {
		return propertyResolver.replaceRequiredPlaceholders(value, placeholderResolver);
	}
}
