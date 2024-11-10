package io.basc.framework.observe.properties;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.convert.lang.ValueWrapper;

public class DynamicValueRegistry<K> extends DynamicMap<K, ValueWrapper> implements ObservableValueFactory<K> {

	public DynamicValueRegistry(Map<K, ValueWrapper> targetMap,
			Function<? super Properties, ? extends Map<K, ValueWrapper>> propertiesMapper) {
		super(targetMap, propertiesMapper);
	}
}
