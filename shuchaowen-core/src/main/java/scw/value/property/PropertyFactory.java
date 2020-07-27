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
import scw.io.ResourceUtils;
import scw.io.event.ObservableResource;
import scw.io.event.ObservableResourceEvent;
import scw.io.event.ObservableResourceEventListener;
import scw.io.support.ResourceOperations;
import scw.util.MultiEnumeration;
import scw.value.AnyValue;
import scw.value.StringValue;
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

	public boolean isPriorityOfUseSelf() {
		return priorityOfUseSelf;
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

	protected Value put(String key, Value value) {
		Value v = map.put(key, value);
		PropertyEvent event = null;
		if (v == null) {
			event = new PropertyEvent(this, EventType.CREATE, key, value);
		} else {
			if (!v.equals(value)) {
				event = new PropertyEvent(this, EventType.UPDATE, key, value);
			}
		}

		if (event != null) {
			eventDispatcher.publishEvent(key, event);
		}
		return v;
	}

	public Value put(String key, Object value) {
		return put(key, value, false);
	}

	public Value put(String key, Object value, boolean format) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return put(key, toValue(value, format));
	}

	private Value toValue(Object value, boolean format) {
		Value v;
		if (value instanceof Value) {
			v = (Value) value;
		} else if (value instanceof String) {
			v = format ? new StringFormatValue((String) value) : new StringValue((String) value);
		} else {
			v = new AnyValue(value);
		}
		return v;
	}

	protected Value putIfAbsent(String key, Value value) {
		Value v = map.putIfAbsent(key, value);
		if (v != null) {
			eventDispatcher.publishEvent(key, new PropertyEvent(this, EventType.CREATE, key, value));
		}
		return v;
	}

	public Value putIfAbsent(String key, Object value) {
		return putIfAbsent(key, value, false);
	}

	public Value putIfAbsent(String key, Object value, boolean format) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return putIfAbsent(key, toValue(value, format));
	}

	public void clear() {
		Map<String, Value> cloneMap = new HashMap<String, Value>(map);
		map.clear();
		for (Entry<String, Value> entry : cloneMap.entrySet()) {
			eventDispatcher.publishEvent(entry.getKey(),
					new PropertyEvent(this, EventType.DELETE, entry.getKey(), entry.getValue()));
		}
	}

	public PropertiesRegistration loadProperties(String resource, String charsetName) {
		return loadProperties(null, resource, charsetName);
	}

	public PropertiesRegistration loadProperties(String keyPrefix, String resource, String charsetName) {
		return loadProperties(keyPrefix, ResourceUtils.getResourceOperations(), resource, charsetName);
	}

	public PropertiesRegistration loadProperties(final String keyPrefix, ResourceOperations resourceOperations,
			String resource, String charsetName) {
		return loadProperties(keyPrefix, resourceOperations, resource, charsetName, false);
	}

	public PropertiesRegistration loadProperties(final String keyPrefix, ResourceOperations resourceOperations,
			String resource, String charsetName, boolean format) {
		ObservableResource<Properties> res = resourceOperations.getProperties(resource, charsetName);
		if (res.getResource() != null) {
			loadProperties(keyPrefix, res.getResource(), format);
		}

		return new PropertiesRegistration(keyPrefix, res);
	}

	public void loadProperties(Properties properties) {
		loadProperties(null, properties);
	}

	public void loadProperties(String keyPrefix, Properties properties) {
		loadProperties(keyPrefix, properties, false);
	}

	public void loadProperties(String keyPrefix, Properties properties, boolean format) {
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
				put(keyPrefix == null ? key.toString() : (keyPrefix + key.toString()), value, format);
			}
		}
	}

	public class PropertiesRegistration implements EventRegistration {
		private final ObservableResource<Properties> resource;
		private EventRegistration eventRegistration;
		private final String keyPrefix;

		public PropertiesRegistration(String keyPrefix, ObservableResource<Properties> resource) {
			this.keyPrefix = keyPrefix;
			this.resource = resource;
		}

		public ObservableResource<Properties> getResource() {
			return resource;
		}

		public boolean isRegisterListener() {
			return eventRegistration != null;
		}

		public PropertyFactory getPropertyFactory() {
			return PropertyFactory.this;
		}

		public PropertyFactory registerListener() {
			if (isRegisterListener()) {
				return getPropertyFactory();
			}

			this.eventRegistration = resource.registerListener(new ObservableResourceEventListener<Properties>() {

				public void onEvent(ObservableResourceEvent<Properties> event) {
					getPropertyFactory().loadProperties(keyPrefix, event.getSource());
				}
			});
			return getPropertyFactory();
		}

		public void unregister() {
			if (eventRegistration != null) {
				eventRegistration.unregister();
			}
		}
	}

	class StringFormatValue extends StringValue {
		private static final long serialVersionUID = 1L;

		public StringFormatValue(String value) {
			super(value);
		}

		@Override
		public String getAsString() {
			String value = super.getAsString();
			return format(value, true);
		}
	}
}
