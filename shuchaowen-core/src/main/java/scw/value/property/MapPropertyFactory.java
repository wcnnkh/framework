package scw.value.property;

import java.util.HashMap;
import java.util.Map;

import scw.value.Value;

public class MapPropertyFactory extends AbstractMapPropertyFactory {
	private Map<String, Value> targetMap;

	public MapPropertyFactory() {
		this.targetMap = new HashMap<String, Value>();
	}

	public MapPropertyFactory(Map<String, Value> targetMap) {
		this.targetMap = targetMap;
	}

	@Override
	protected Map<String, Value> getTargetMap() {
		return targetMap;
	}
}
