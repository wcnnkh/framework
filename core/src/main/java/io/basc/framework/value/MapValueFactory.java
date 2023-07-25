package io.basc.framework.value;

import java.util.Map;
import java.util.Set;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.ChangeType;
import io.basc.framework.event.broadcast.BroadcastEventRegistry;
import io.basc.framework.event.support.DynamicMap;
import io.basc.framework.util.element.ElementSet;
import io.basc.framework.util.element.Elements;

public class MapValueFactory<K> extends DynamicMap<K, Value> implements DynamicValueFactory<K> {

	public MapValueFactory() {
	}

	public MapValueFactory(Map<K, Value> map) {
		super(map);
	}

	public Value get(K key) {
		Value value = super.getUnsafeMap().get(key);
		return value == null ? Value.EMPTY : value;
	}

	@Override
	public BroadcastEventRegistry<ChangeEvent<Elements<K>>> getKeyEventRegistry() {
		return (listener) -> {
			return getEventDispatcher().registerListener((event) -> {
				Set<K> changeKeys = event.getChangeType() == ChangeType.DELETE ? event.getOldSource().keySet()
						: event.getSource().keySet();
				Elements<K> keys = new ElementSet<>(changeKeys);
				ChangeEvent<Elements<K>> changeEvent = new ChangeEvent<>(event, keys);
				listener.onEvent(changeEvent);
			});
		};
	}

}
