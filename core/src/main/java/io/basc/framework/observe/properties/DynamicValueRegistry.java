package io.basc.framework.observe.properties;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.value.Value;

public class DynamicValueRegistry<K> extends DynamicMap<K, Value> implements ObservableValueFactory<K> {

	public DynamicValueRegistry(Map<K, Value> targetMap,
			Function<? super Properties, ? extends Map<K, Value>> propertiesMapper) {
		super(targetMap, propertiesMapper);
	}
}
