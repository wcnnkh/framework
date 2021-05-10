package scw.util.placeholder.support;

import java.util.Map;
import java.util.Properties;

import scw.util.placeholder.PlaceholderResolver;
import scw.value.Value;
import scw.value.ValueFactory;
import scw.value.support.MapValueFactory;
import scw.value.support.PropertiesValueFactory;

public class DefaultPlaceholderResolver implements PlaceholderResolver {
	private final ValueFactory<String> valueFactory;

	public DefaultPlaceholderResolver(ValueFactory<String> valueFactory) {
		this.valueFactory = valueFactory;
	}
	
	public DefaultPlaceholderResolver(Map<String, ?> map) {
		this(new MapValueFactory<String>(map));
	}
	
	public DefaultPlaceholderResolver(Properties properties){
		this(new PropertiesValueFactory<String>(properties));
	}

	public String resolvePlaceholder(String placeholderName) {
		Value value = valueFactory.getValue(placeholderName);
		return value == null ? null : value.getAsString();
	}
}
