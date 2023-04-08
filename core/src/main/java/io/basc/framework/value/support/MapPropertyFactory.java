package io.basc.framework.value.support;

import java.util.Map;

import io.basc.framework.util.ElementSet;
import io.basc.framework.util.Elements;
import io.basc.framework.value.PropertyFactory;

public class MapPropertyFactory extends MapValueFactory<String> implements PropertyFactory {

	public MapPropertyFactory(Map<String, ?> map) {
		super(map);
	}

	@Override
	public Elements<String> keys() {
		return new ElementSet<>(map.keySet());
	}

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

}
