package io.basc.framework.value;

import java.util.Map;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.BroadcastEventRegistry;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.support.DynamicMap;
import io.basc.framework.event.support.ObservableMapRegistry;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;
import io.basc.framework.util.Elements;

public class ObservableValueFactory<K> extends ObservableMapRegistry<K, Value> implements DynamicValueFactory<K> {

	public ObservableValueFactory(Function<? super Properties, ? extends Map<K, Value>> propertiesMapper) {
		this(new DynamicMap<>(), new StandardBroadcastEventDispatcher<>(), propertiesMapper);
	}

	public ObservableValueFactory(DynamicMap<K, Value> master,
			BroadcastEventDispatcher<ChangeEvent<Elements<K>>> keyEventDispatcher,
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
	public BroadcastEventRegistry<ChangeEvent<Elements<K>>> getKeyEventRegistry() {
		return getKeyEventDispatcher();
	}
}
