package io.basc.framework.core.env;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.ValueFactory;

public interface Properties extends PropertyMapping<Property>, ValueFactory<String, Property> {

	@Override
	default Elements<Property> getAccesses(String key) {
		Property property = get(key);
		if (property == null) {
			return Elements.empty();
		}
		return Elements.singleton(property);
	}

	default boolean containsKey(String key) {
		Property property = get(key);
		return property != null && property.isPresent();
	}

	@Override
	Property get(String key);

	@Override
	default Elements<KeyValue<String, Property>> getElements() {
		return keys().map((key) -> KeyValue.of(key, get(key)));
	}

	@Override
	default Elements<Property> getValues(String key) {
		Property property = get(key);
		if (property == null) {
			return Elements.empty();
		}
		return Elements.singleton(property);
	}

	@Override
	Elements<String> keys();
}
