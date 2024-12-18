package io.basc.framework.observe.properties;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.core.convert.Any;

public class DynamicValueRegistry<K> extends DynamicMap<K, Any> implements ObservableValueFactory<K> {

	public DynamicValueRegistry(Map<K, Any> targetMap,
			Function<? super Properties, ? extends Map<K, Any>> propertiesMapper) {
		super(targetMap, propertiesMapper);
	}
}
