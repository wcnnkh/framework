package io.basc.framework.value;

import java.util.Map;
import java.util.Set;

import io.basc.framework.event.broadcast.BroadcastEventRegistry;
import io.basc.framework.observe.ObservableEvent;
import io.basc.framework.observe.ChangeType;
import io.basc.framework.util.element.ElementSet;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.observe.support.ObservableMap;

public class MapValueFactory<K> extends ObservableMap<K, Value> implements DynamicValueFactory<K> {

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
	public BroadcastEventRegistry<ObservableEvent<Elements<K>>> getKeyEventRegistry() {
		return (listener) -> {
			return getEventDispatcher().registerListener((event) -> {
				Set<K> changeKeys = event.getChangeType() == ChangeType.DELETE ? event.getOldSource().keySet()
						: event.getSource().keySet();
				Elements<K> keys = new ElementSet<>(changeKeys);
				ObservableEvent<Elements<K>> changeEvent = new ObservableEvent<>(event, keys);
				listener.onEvent(changeEvent);
			});
		};
	}

}
