package scw.value.property;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import scw.compatible.CompatibleUtils;
import scw.compatible.map.CompatibleMap;
import scw.core.Assert;
import scw.core.utils.CollectionUtils;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.event.method.MultiEventRegistration;
import scw.event.support.DefaultEventDispatcher;
import scw.event.support.EventType;
import scw.io.event.ObservableResource;
import scw.io.event.ObservableResourceEvent;
import scw.io.event.ObservableResourceEventListener;
import scw.io.support.ResourceOperations;
import scw.util.MultiEnumeration;
import scw.value.AnyValue;
import scw.value.StringValueFactory;
import scw.value.Value;

public class PropertyFactory extends StringValueFactory implements BasePropertyFactory {
	private final List<BasePropertyFactory> basePropertyFactories;
	private final CompatibleMap<String, Value> map;
	private final NamedEventDispatcher<PropertyEvent> eventDispatcher;
	private final boolean priorityOfUseSelf;

	/**
	 * @param concurrent
	 * @param priorityOfUseSelf
	 *            是否优先使用自身的值
	 */
	public PropertyFactory(boolean concurrent, boolean priorityOfUseSelf) {
		this.basePropertyFactories = concurrent ? new CopyOnWriteArrayList<BasePropertyFactory>()
				: new LinkedList<BasePropertyFactory>();
		this.priorityOfUseSelf = priorityOfUseSelf;
		this.map = CompatibleUtils.createMap(concurrent);
		this.eventDispatcher = new DefaultEventDispatcher<PropertyEvent>(concurrent);
	}

	public void addFirstBasePropertyFactory(BasePropertyFactory basePropertyFactory) {
		if (basePropertyFactory == null) {
			return;
		}

		basePropertyFactories.add(0, basePropertyFactory);
	}

	public void addLastBasePropertyFactory(BasePropertyFactory basePropertyFactory) {
		if (basePropertyFactory == null) {
			return;
		}

		basePropertyFactories.add(basePropertyFactory);
	}

	public void addLastBasePropertyFactory(List<BasePropertyFactory> basePropertyFactories) {
		if (CollectionUtils.isEmpty(basePropertyFactories)) {
			return;
		}

		for (BasePropertyFactory propertyFactory : basePropertyFactories) {
			addLastBasePropertyFactory(propertyFactory);
		}
	}

	public List<BasePropertyFactory> getBasePropertyFactories() {
		return Collections.unmodifiableList(basePropertyFactories);
	}

	@Override
	public Value get(String key) {
		if (priorityOfUseSelf) {
			Value value = map.get(key);
			if (value != null) {
				return value;
			}
		}

		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			Value value = basePropertyFactory.get(key);
			if (value != null) {
				return value;
			}
		}

		Value value = super.get(key);
		if (value == null && !priorityOfUseSelf) {
			value = map.get(key);
		}
		return value;
	}

	public Enumeration<String> enumerationKeys() {
		List<Enumeration<String>> enumerations = new LinkedList<Enumeration<String>>();
		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			enumerations.add(basePropertyFactory.enumerationKeys());
		}
		enumerations.add(Collections.enumeration(map.keySet()));
		return new MultiEnumeration<String>(enumerations);
	}

	public boolean containsKey(String key) {
		if (map.containsKey(key)) {
			return true;
		}

		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			if (basePropertyFactory.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public EventRegistration registerListener(String key, EventListener<PropertyEvent> eventListener) {
		EventRegistration registration = eventDispatcher.registerListener(key, eventListener);
		EventRegistration[] registrations = new EventRegistration[basePropertyFactories.size() + 1];
		registrations[0] = registration;
		int index = 1;
		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			registrations[index++] = basePropertyFactory.registerListener(key, eventListener);
		}
		return new MultiEventRegistration(registrations);
	}

	public void unregister(String name) {
		eventDispatcher.unregister(name);
	}

	public void publishEvent(String name, PropertyEvent event) {
		eventDispatcher.publishEvent(name, event);
	}

	public Value remove(String key) {
		Assert.requiredArgument(key != null, "key");
		Value v = map.remove(key);
		if (v != null) {
			eventDispatcher.publishEvent(key, new PropertyEvent(this, EventType.DELETE, key, v));
		}
		return v;
	}

	public Value put(String key, Value value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		Value v = map.put(key, value);
		eventDispatcher.publishEvent(key,
				new PropertyEvent(this, v == null ? EventType.CREATE : EventType.UPDATE, key, value));
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
			eventDispatcher.publishEvent(key, new PropertyEvent(this, EventType.CREATE, key, value));
		}
		return v;
	}

	public Value putIfAbsent(String key, Object value) {
		return putIfAbsent(key, new AnyValue(value));
	}

	public void clear() {
		Map<String, Value> cloneMap = new HashMap<String, Value>(map);
		map.clear();
		for (Entry<String, Value> entry : cloneMap.entrySet()) {
			eventDispatcher.publishEvent(entry.getKey(),
					new PropertyEvent(this, EventType.DELETE, entry.getKey(), entry.getValue()));
		}
	}

	public PropertiesRegistration loadProperties(final String keyPrefix, ResourceOperations resourceOperations,
			String resource, String charsetName) {
		ObservableResource<Properties> res = resourceOperations.getProperties(resource, charsetName);
		if (res.getResource() != null) {
			loadProperties(keyPrefix, res.getResource());
		}

		return new PropertiesRegistration(res, keyPrefix);
	}

	public void loadProperties(Properties properties) {
		loadProperties(null, properties);
	}

	public void loadProperties(String keyPrefix, Properties properties) {
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
				put(keyPrefix == null ? key.toString() : (keyPrefix + key.toString()), value);
			}
		}
	}

	public class PropertiesRegistration {
		private final ObservableResource<Properties> resource;
		private EventRegistration eventRegistration;
		private final String keyPrefix;

		public PropertiesRegistration(ObservableResource<Properties> resource, String keyPrefix) {
			this.resource = resource;
			this.keyPrefix = keyPrefix;
		}

		public ObservableResource<Properties> getResource() {
			return resource;
		}

		public EventRegistration getEventRegistration() {
			return eventRegistration;
		}

		public boolean isRegisterListener() {
			return eventRegistration != null;
		}

		public boolean registerListener() {
			if (isRegisterListener()) {
				return false;
			}

			eventRegistration = resource.registerListener(new ObservableResourceEventListener<Properties>() {

				public void onEvent(ObservableResourceEvent<Properties> event) {
					loadProperties(keyPrefix, event.getSource());
				}
			});
			return true;
		}
	}
}
