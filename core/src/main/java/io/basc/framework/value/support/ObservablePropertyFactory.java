package io.basc.framework.value.support;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.event.support.StandardObservableProperties;
import io.basc.framework.lang.Nullable;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.PropertyWrapper;
import io.basc.framework.value.Value;

public class ObservablePropertyFactory extends StandardObservableProperties<String, Value> implements PropertyFactory {

	public ObservablePropertyFactory() {
		this(null);
	}

	public ObservablePropertyFactory(@Nullable PropertyWrapper propertyWrapper) {
		super((properties) -> {
			if (properties == null) {
				return Collections.emptyMap();
			}

			Map<String, Value> map = new LinkedHashMap<>();
			for (Entry<Object, Object> entry : properties.entrySet()) {
				Object key = entry.getKey();
				if (key == null) {
					continue;
				}

				String propertyName = String.valueOf(key);
				Value value = (propertyWrapper == null ? PropertyWrapper.CREATOR : propertyWrapper).wrap(propertyName,
						entry.getValue());
				map.put(propertyName, value);
			}
			return map;
		});
	}

	@Override
	public Value get(String key) {
		Value value = super.get(key);
		return value == null ? Value.EMPTY : value;
	}

	@Override
	public Iterator<String> iterator() {
		return keySet().iterator();
	}
}
