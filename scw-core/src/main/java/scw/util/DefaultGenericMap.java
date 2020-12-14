package scw.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultGenericMap<K, V> extends AbstractGenericMap<K, V> {
	private final java.util.Map<K, V> targetMap;
	
	public DefaultGenericMap(boolean concurrent) {
		this(concurrent ? new ConcurrentHashMap<K, V>() : new HashMap<K, V>());
	}

	public DefaultGenericMap(java.util.Map<K, V> targetMap) {
		this.targetMap = targetMap;
	}

	@Override
	protected Map<K, V> getTargetMap() {
		return targetMap;
	}
}
