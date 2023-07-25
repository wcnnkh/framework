package io.basc.framework.value;

import java.util.Map;

import io.basc.framework.util.element.ElementSet;
import io.basc.framework.util.element.Elements;

public class MapPropertyFactory extends MapValueFactory<String> implements DynamicPropertyFactory {

	public MapPropertyFactory() {
	}

	public MapPropertyFactory(Map<String, Value> map) {
		super(map);
	}

	@Override
	public Elements<String> keys() {
		return new ElementSet<>(getUnsafeMap().keySet());
	}

}
