package io.basc.framework.util.placeholder.support;

import io.basc.framework.util.placeholder.PlaceholderResolver;
import io.basc.framework.value.Value;
import io.basc.framework.value.ValueFactory;
import io.basc.framework.value.support.MapValueFactory;
import io.basc.framework.value.support.PropertiesValueFactory;

import java.util.Map;
import java.util.Properties;

public class DefaultPlaceholderResolver implements PlaceholderResolver {
	private final ValueFactory<String> valueFactory;

	public DefaultPlaceholderResolver(ValueFactory<String> valueFactory) {
		this.valueFactory = valueFactory;
	}

	public DefaultPlaceholderResolver(Map<String, ?> map) {
		this(new MapValueFactory<String>(map));
	}

	public DefaultPlaceholderResolver(Properties properties) {
		this(new PropertiesValueFactory<String>(properties));
	}

	public String resolvePlaceholder(String placeholderName) {
		Value value = valueFactory.getValue(placeholderName);
		return value == null ? null : value.getAsString();
	}
}
