package io.basc.framework.core.env;

import java.util.Iterator;
import java.util.stream.Stream;

import io.basc.framework.core.convert.transform.MappingWrapper;
import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.KeyValuesWrapper;
import io.basc.framework.util.ValueFactoryWrapper;

public interface PropertiesWrapper<W extends Properties> extends Properties, KeyValuesWrapper<String, Property, W>,
		ValueFactoryWrapper<String, Property, W>, MappingWrapper<String, Property, W> {

	@Override
	default Iterator<KeyValue<String, Property>> iterator() {
		return getSource().iterator();
	}

	@Override
	default boolean isEmpty() {
		return getSource().isEmpty();
	}

	@Override
	default Stream<KeyValue<String, Property>> stream() {
		return getSource().stream();
	}

	@Override
	default Elements<Property> getAccesses(String key) {
		return getSource().getAccesses(key);
	}

	@Override
	default Elements<KeyValue<String, Property>> getElements() {
		return getSource().getElements();
	}

	@Override
	default Elements<String> keys() {
		return getSource().keys();
	}
}
