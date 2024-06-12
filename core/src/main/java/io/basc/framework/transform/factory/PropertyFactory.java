package io.basc.framework.transform.factory;

import io.basc.framework.convert.lang.Value;
import io.basc.framework.convert.lang.ValueFactory;
import io.basc.framework.transform.Properties;
import io.basc.framework.transform.Property;
import io.basc.framework.transform.ReadOnlyProperty;
import io.basc.framework.util.element.Elements;

public interface PropertyFactory extends ValueFactory<String>, Properties {

	default boolean containsKey(String key) {
		Value value = get(key);
		return value != null && value.isPresent();
	}

	@Override
	default Elements<Property> getElements() {
		return keys().map((key) -> new ReadOnlyProperty(key, get(key)));
	}

	Elements<String> keys();
}
