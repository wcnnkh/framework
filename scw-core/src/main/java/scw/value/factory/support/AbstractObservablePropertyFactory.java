package scw.value.factory.support;

import scw.util.DefaultPropertyResolver;
import scw.util.PropertyResolver;
import scw.util.DefaultPlaceholderResolver;
import scw.value.factory.ObservablePropertyFactory;

public abstract class AbstractObservablePropertyFactory extends AbstractConvertibleObservableValueFactory<String> implements ObservablePropertyFactory, PropertyResolver {
	private final DefaultPropertyResolver propertyResolver = new DefaultPropertyResolver(new DefaultPlaceholderResolver(this));
	
	public String resolvePlaceholders(String text) {
		return propertyResolver.resolvePlaceholders(text);
	}
	
	public String resolveRequiredPlaceholders(String text)
			throws IllegalArgumentException {
		return propertyResolver.resolveRequiredPlaceholders(text);
	}
}
