package io.basc.framework.value;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.event.broadcast.BroadcastEventDispatcher;
import io.basc.framework.event.broadcast.BroadcastEventRegistry;
import io.basc.framework.event.broadcast.support.StandardBroadcastEventDispatcher;
import io.basc.framework.observe.ObservableEvent;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.observe.support.ObservableMap;
import io.basc.framework.value.observe.support.ObservableMapRegistry;

public class ObservableValueFactory<K> extends ObservableMapRegistry<K, Value> implements DynamicValueFactory<K> {

	public ObservableValueFactory(Function<? super Properties, ? extends Map<K, Value>> propertiesMapper) {
		this(new ObservableMap<>(), new StandardBroadcastEventDispatcher<>(), propertiesMapper);
	}

	public ObservableValueFactory(ObservableMap<K, Value> master,
			BroadcastEventDispatcher<ObservableEvent<Elements<K>>> keyEventDispatcher,
			Function<? super Properties, ? extends Map<K, Value>> propertiesMapper) {
		super(master, keyEventDispatcher, propertiesMapper);
	}

	@Override
	public Value get(K key) {
		Value value = getReadonlyMap().get(key);
		if (value != null && value.isPresent()) {
			return value;
		}
		return Value.EMPTY;
	}

	@Override
	public BroadcastEventRegistry<ObservableEvent<Elements<K>>> getKeyEventRegistry() {
		return getKeyEventDispatcher();
	}
}
