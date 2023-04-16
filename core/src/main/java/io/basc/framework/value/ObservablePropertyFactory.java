package io.basc.framework.value;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.event.BroadcastEventDispatcher;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.support.DynamicMap;
import io.basc.framework.event.support.StandardBroadcastEventDispatcher;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.util.ElementSet;
import io.basc.framework.util.Elements;

public class ObservablePropertyFactory extends ObservableValueFactory<String> implements DynamicPropertyFactory {
	private static final Function<Properties, Map<String, Value>> CONVERTER = (properties) -> {
		if (CollectionUtils.isEmpty(properties)) {
			return Collections.emptyMap();
		}

		Map<String, Value> map = new LinkedHashMap<>(properties.size());
		for (Entry<?, ?> entry : properties.entrySet()) {
			map.put(String.valueOf(entry.getKey()), Value.of(entry.getValue()));
		}
		return map;
	};

	public ObservablePropertyFactory() {
		this(new StandardBroadcastEventDispatcher<>());
	}

	public ObservablePropertyFactory(BroadcastEventDispatcher<ChangeEvent<Elements<String>>> keyEventDispatcher) {
		super(new DynamicMap<>(), keyEventDispatcher, CONVERTER);
	}

	@Override
	public boolean containsKey(String key) {
		return getReadonlyMap().containsKey(key);
	}

	@Override
	public Elements<String> keys() {
		return new ElementSet<>(getReadonlyMap().keySet());
	}
}
