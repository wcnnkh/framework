package io.basc.framework.event.support;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import io.basc.framework.event.EventDispatcher;
import io.basc.framework.event.Observable;
import io.basc.framework.event.ObservableChangeEvent;
import io.basc.framework.util.Assert;
import io.basc.framework.util.Registration;

public class StandardObservableProperties<K, V> extends StandardObservableMap<K, V> {
	private final Function<? super Properties, ? extends Map<K, V>> propertiesMapper;

	public StandardObservableProperties(Function<? super Properties, ? extends Map<K, V>> propertiesMapper) {
		this(new ConcurrentHashMap<>(), propertiesMapper);
	}

	public StandardObservableProperties(Map<K, V> sourceMap,
			Function<? super Properties, ? extends Map<K, V>> propertiesMapper) {
		this(new SimpleEventDispatcher<>(), sourceMap, propertiesMapper);
	}

	public StandardObservableProperties(EventDispatcher<ObservableChangeEvent<Map<K, V>>> eventDispatcher, Map<K, V> sourceMap,
			Function<? super Properties, ? extends Map<K, V>> propertiesMapper) {
		super(eventDispatcher, sourceMap);
		Assert.requiredArgument(propertiesMapper != null, "propertiesMapper");
		this.propertiesMapper = propertiesMapper;
	}

	public Registration registerProperties(Observable<? extends Properties> properties) {
		return register(properties.map(propertiesMapper));
	}
}
