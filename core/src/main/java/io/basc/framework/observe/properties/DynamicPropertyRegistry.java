package io.basc.framework.observe.properties;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.Lock;

import io.basc.framework.observe.ChangeType;
import io.basc.framework.observe.value.ObservableValue;
import io.basc.framework.util.Registration;
import io.basc.framework.util.element.Elements;
import io.basc.framework.value.EditablePropertyFactory;
import io.basc.framework.value.Value;

public class DynamicPropertyRegistry extends DynamicValueRegistry<String>
		implements ObservablePropertyFactory, EditablePropertyFactory {
	private final PropertyFactories factories = new PropertyFactories();
	private final PropertyWrapper propertyWrapper;

	public DynamicPropertyRegistry() {
		this(PropertyWrapper.CREATOR);
	}

	public DynamicPropertyRegistry(PropertyWrapper propertyWrapper) {
		super(new LinkedHashMap<>(), (properties) -> {
			if (properties.isEmpty()) {
				return Collections.emptyMap();
			}

			Map<String, Value> map = new LinkedHashMap<>(properties.size());
			Enumeration<Object> enumeration = properties.keys();
			while (enumeration.hasMoreElements()) {
				Object key = enumeration.nextElement();
				if (key == null) {
					continue;
				}
				String keyStr = String.valueOf(key);
				Object value = properties.get(key);
				map.put(keyStr, propertyWrapper.wrap(keyStr, value));
			}
			return map;
		});
		this.propertyWrapper = propertyWrapper;
		factories.registerPropertyListener(this::publishBatchEvent);
	}

	@Override
	public Value get(String key) {
		Value value = get((Object) key);
		return (value == null || !value.isPresent()) ? factories.get(key) : value;
	}

	public PropertyFactories getFactories() {
		return factories;
	}

	@Override
	public Elements<String> keys() {
		return Elements.of(keySet()).concat(factories.keys());
	}

	@Override
	public Value put(String key, Object value) {
		return put(key, value instanceof Value ? ((Value) value) : propertyWrapper.wrap(key, value));
	}

	@Override
	public Registration registerMap(ObservableMap<String, Value> observableMap) {
		return factories.register(new ObservableMapToObservablePropertyFactory(observableMap));
	}

	@Override
	public Registration registerObservableProperties(ObservableProperties observableProperties) {
		return factories.register(observableProperties);
	}

	@Override
	public Registration registerProperties(ObservableValue<? extends Properties> properties) {
		ObservableProperties observableProperties = new ObservableProperties();
		Registration registration = observableProperties.bind(properties);
		return registration.and(registerObservableProperties(observableProperties));
	}

	@Override
	public Registration registerValue(ObservableValue<? extends Map<String, Value>> observableValue) {
		ObservableProperties observableProperties = new ObservableProperties(propertyWrapper);
		Registration registration = observableProperties.bind(observableValue);
		registration = registration.and(registerObservableProperties(observableProperties));
		return registration;
	}

	@Override
	public Value remove(String key) {
		Lock lock = getReadWriteLock().writeLock();
		lock.lock();
		try {
			boolean exists = getTargetMap().containsKey(key);
			Value vlaue = getTargetMap().remove(key);
			if (exists) {
				publishEvent(new PropertyChangeEvent<>(this, ChangeType.DELETE, key, vlaue));
			}
		} finally {
			lock.unlock();
		}
		return remove((Object) key);
	}
}
