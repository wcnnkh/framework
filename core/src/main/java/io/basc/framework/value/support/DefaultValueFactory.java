package io.basc.framework.value.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.MultiEventRegistration;
import io.basc.framework.event.NamedEventDispatcher;
import io.basc.framework.event.support.SimpleNamedEventDispatcher;
import io.basc.framework.factory.ConfigurableServices;
import io.basc.framework.lang.Nullable;
import io.basc.framework.util.Assert;
import io.basc.framework.value.ConfigurableValueFactory;
import io.basc.framework.value.Value;
import io.basc.framework.value.ValueFactory;

public class DefaultValueFactory<K, F extends ValueFactory<K>> implements ConfigurableValueFactory<K> {
	private final Map<K, Value> valueMap;
	private final NamedEventDispatcher<K, ChangeEvent<K>> eventDispatcher;
	private final ConfigurableServices<F> tandemFactories = new ConfigurableServices<F>();

	public DefaultValueFactory() {
		this(new ConcurrentHashMap<>());
	}

	public DefaultValueFactory(@Nullable NamedEventDispatcher<K, ChangeEvent<K>> eventDispatcher) {
		this(null, eventDispatcher);
	}

	public DefaultValueFactory(@Nullable Map<K, Value> valueMap) {
		this(valueMap, null);
	}

	public DefaultValueFactory(@Nullable Map<K, Value> valueMap,
			@Nullable NamedEventDispatcher<K, ChangeEvent<K>> eventDispatcher) {
		this.valueMap = valueMap == null ? new ConcurrentHashMap<>() : valueMap;
		this.eventDispatcher = eventDispatcher == null ? new SimpleNamedEventDispatcher<>() : eventDispatcher;
	}

	public Map<K, Value> getValueMap() {
		return this.valueMap;
	}

	public ConfigurableServices<F> getTandemFactories() {
		return this.tandemFactories;
	}

	public Value getValue(K key) {
		Value value = valueMap.get(key);
		if (value != null) {
			return value;
		}

		for (F factory : getTandemFactories()) {
			if (factory == null) {
				continue;
			}
			value = factory.getValue(key);
			if (value != null) {
				return value;
			}
		}
		return null;
	}

	public boolean containsKey(K key) {
		if (valueMap.containsKey(key)) {
			return true;
		}

		for (F factory : getTandemFactories()) {
			if (factory.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public EventRegistration registerListener(final K key, final EventListener<ChangeEvent<K>> eventListener) {
		EventRegistration registration1 = eventDispatcher.registerListener(key, eventListener);
		if (getTandemFactories().isEmpty()) {
			return registration1;
		}

		List<EventRegistration> registrations = new ArrayList<EventRegistration>(8);
		registrations.add(registration1);
		for (F factory : getTandemFactories()) {
			EventRegistration registration = factory.registerListener(key, eventListener);
			registrations.add(registration);
		}
		return new MultiEventRegistration(registrations.toArray(new EventRegistration[0]));
	}

	public boolean remove(K key) {
		Assert.requiredArgument(key != null, "key");
		Value value = valueMap.remove(key);
		return value != null;
	}

	public boolean put(K key, Value value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		valueMap.put(key, value);
		return true;
	}

	public boolean putIfAbsent(K key, Value value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return valueMap.putIfAbsent(key, value) == null;
	}

	public void clear() {
		valueMap.clear();
	}
}
