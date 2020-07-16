package scw.value.property;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map.Entry;
import java.util.Properties;

import scw.compatible.CompatibleUtils;
import scw.compatible.map.CompatibleMap;
import scw.core.Assert;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.event.support.DefaultEventDispatcher;
import scw.event.support.EventType;
import scw.io.support.ResourceOperations;
import scw.util.MultiEnumeration;
import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.Value;

public abstract class MapPropertyFactory extends PropertyFactory {
	private final CompatibleMap<String, Value> map;
	private final NamedEventDispatcher<PropertyEvent> eventDispatcher;

	public MapPropertyFactory(boolean concurrent) {
		this.map = CompatibleUtils.createMap(concurrent);
		this.eventDispatcher = new DefaultEventDispatcher<PropertyEvent>(
				concurrent);
	}

	@Override
	public Value get(String key) {
		Value value = map.get(key);
		if (value != null) {
			return value;
		}
		return super.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<String> enumerationKeys() {
		Enumeration<String> e1 = Collections.enumeration(map.keySet());
		Enumeration<String> e2 = super.enumerationKeys();
		return new MultiEnumeration<String>(e1, e2);
	}

	public Value remove(String key) {
		Assert.requiredArgument(key != null, "key");
		Value v = map.remove(key);
		if(v != null){
			eventDispatcher.publishEvent(key, new PropertyEvent(EventType.DELETE, key, v));
		}
		return v;
	}

	public Value put(String key, Value value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		Value v = map.put(key, value);
		eventDispatcher.publishEvent(key, new PropertyEvent(
				v == null ? EventType.CREATE : EventType.UPDATE, key, value));
		return v;
	}

	public Value put(String key, Object value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return put(key, new AnyValue(value));
	}

	public Value putIfAbsent(String key, Value value) {
		Value v = map.putIfAbsent(key, value);
		if (v != null) {
			eventDispatcher.publishEvent(key, new PropertyEvent(
					EventType.CREATE, key, value));
		}
		return v;
	}

	public Value putIfAbsent(String key, Object value) {
		return putIfAbsent(key, new AnyValue(value));
	}

	protected Value createValue(Object value) {
		if (value instanceof Value) {
			return (Value) value;
		}

		return new StringValue(value == null ? null : value.toString());
	}

	public void clear() {
		map.clear();
		for (Entry<String, Value> entry : map.entrySet()) {
			eventDispatcher.publishEvent(entry.getKey(), new PropertyEvent(
					EventType.DELETE, entry.getKey(), entry.getValue()));
		}
	}

	public void loadProperties(ResourceOperations resourceOperations,
			String resource) {
		if (resourceOperations.isExist(resource)) {
			Properties properties = resourceOperations.getFormattedProperties(
					resource).getResource();
			if (properties != null) {
				loadProperties(properties);
			}
		}
	}

	public void loadProperties(Properties properties) {
		if (properties != null) {
			for (Entry<Object, Object> entry : properties.entrySet()) {
				Object key = entry.getKey();
				if (key == null) {
					continue;
				}

				Object value = entry.getValue();
				if (value == null) {
					continue;
				}

				put(key.toString(), value);
			}
		}
	}

	public void loadProperties(ResourceOperations resourceOperations,
			String resource, boolean putIfAbsent) {
		if (putIfAbsent) {
			if (resourceOperations.isExist(resource)) {
				Properties properties = resourceOperations
						.getFormattedProperties(resource).getResource();
				if (properties != null) {
					loadProperties(properties, putIfAbsent);
				}
			}
		} else {
			loadProperties(resourceOperations, resource);
		}
	}

	public void loadProperties(Properties properties, boolean putIfAbsent) {
		if (putIfAbsent) {
			if (properties != null) {
				for (Entry<Object, Object> entry : properties.entrySet()) {
					Object key = entry.getKey();
					if (key == null) {
						continue;
					}

					Object value = entry.getValue();
					if (value == null) {
						continue;
					}

					putIfAbsent(key.toString(), value);
				}
			}
		} else {
			loadProperties(properties);
		}
	}

	@Override
	public boolean isSupportListener(String key) {
		if (map.containsKey(key)) {
			return true;
		}
		return super.isSupportListener(key);
	}

	@Override
	public EventRegistration registerListener(String key,
			EventListener<PropertyEvent> eventListener) {
		if (map.containsKey(key)) {
			return eventDispatcher.registerListener(key, eventListener);
		}
		return super.registerListener(key, eventListener);
	}

	@Override
	public void unregister(String name) {
		if (map.containsKey(name)) {
			eventDispatcher.unregister(name);
			return;
		}
		super.unregister(name);
	}

	@Override
	public void publishEvent(String name, PropertyEvent event) {
		if (map.containsKey(name)) {
			eventDispatcher.publishEvent(name, event);
			return;
		}
		super.publishEvent(name, event);
	}
}
