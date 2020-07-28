package scw.compatible.map;

import java.util.Map;

public class DefaultCompatibleMap<K, V> extends AbstractCompatibleMap<K, V> {
	private final java.util.Map<K, V> targetMap;

	public DefaultCompatibleMap(java.util.Map<K, V> targetMap) {
		this.targetMap = targetMap;
	}

	@Override
	protected Map<K, V> getTargetMap() {
		return targetMap;
	}
}
