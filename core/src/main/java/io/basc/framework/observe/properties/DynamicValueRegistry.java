package io.basc.framework.observe.properties;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.convert.lang.ObjectValue;

public class DynamicValueRegistry<K> extends DynamicMap<K, ObjectValue> implements ObservableValueFactory<K> {

	public DynamicValueRegistry(Map<K, ObjectValue> targetMap,
			Function<? super Properties, ? extends Map<K, ObjectValue>> propertiesMapper) {
		super(targetMap, propertiesMapper);
	}
}
