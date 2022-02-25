package io.basc.framework.data.memory;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class MapOperations<K, V> extends AbstractMapOperations<K, V> implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<K, V> map = new ConcurrentHashMap<K, V>();

	@Override
	public Map<K, V> getMap() {
		return map;
	}

	@Override
	protected Map<K, V> createMap() {
		return map;
	}
}
