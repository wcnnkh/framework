package scw.value.event;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import scw.compatible.map.DefaultCompatibleMap;
import scw.core.utils.CollectionUtils;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.event.support.DefaultEventDispatcher;
import scw.event.support.EmptyEventDispatcher;
import scw.event.support.EventType;
import scw.io.ResourceUtils;
import scw.io.event.ObservableResource;
import scw.io.event.ObservableResourceEvent;
import scw.io.event.ObservableResourceEventListener;
import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.Value;

public class DefaultDynamicMap extends DefaultCompatibleMap<String, Value> implements DynamicMap {
	private NamedEventDispatcher<ValueEvent> eventDispatcher;

	public DefaultDynamicMap(boolean concurrent) {
		this(new DefaultEventDispatcher<ValueEvent>(concurrent),
				concurrent ? new ConcurrentHashMap<String, Value>() : new HashMap<String, Value>());
	}

	public DefaultDynamicMap(NamedEventDispatcher<ValueEvent> eventDispatcher, Map<String, Value> targetMap) {
		super(targetMap);
		this.eventDispatcher = eventDispatcher == null ? new EmptyEventDispatcher<ValueEvent>() : eventDispatcher;
	}

	public NamedEventDispatcher<ValueEvent> getEventDispatcher() {
		return eventDispatcher;
	}

	protected boolean isSupportedConcurrent() {
		return getTargetMap() instanceof ConcurrentMap;
	}

	@Override
	public Value put(String key, Value value) {
		Value v;
		if (isSupportedConcurrent()) {
			v = super.put(key, value);
		} else {
			synchronized (this) {
				v = super.put(key, value);
			}
		}

		ValueEvent event = null;
		if (v == null) {
			event = new ValueEvent(EventType.CREATE, value);
		} else {
			if (!v.equals(value)) {
				event = new ValueEvent(EventType.UPDATE, value);
			}
		}

		if (event != null) {
			getEventDispatcher().publishEvent(key, event);
		}
		return v;
	}

	@Override
	public Value remove(Object key) {
		Value v;
		if (isSupportedConcurrent()) {
			v = super.remove(key);
		} else {
			synchronized (this) {
				v = super.remove(key);
			}
		}

		if (v != null) {
			getEventDispatcher().publishEvent(key.toString(), new ValueEvent(EventType.DELETE, v));
		}
		return v;
	}

	@Override
	public void clear() {
		Map<String, Value> cloneMap;
		if (isSupportedConcurrent()) {
			cloneMap = new HashMap<String, Value>(this);
			super.clear();
		} else {
			synchronized (this) {
				cloneMap = new HashMap<String, Value>(this);
				super.clear();
			}
		}

		for (Entry<String, Value> entry : cloneMap.entrySet()) {
			getEventDispatcher().publishEvent(entry.getKey(), new ValueEvent(EventType.DELETE, entry.getValue()));
		}
	}

	@Override
	public Value putIfAbsent(String key, Value value) {
		Value v;
		if (isSupportedConcurrent()) {
			v = super.putIfAbsent(key, value);
		} else {
			synchronized (this) {
				v = super.putIfAbsent(key, value);
			}
		}

		if (v != null) {
			getEventDispatcher().publishEvent(key, new ValueEvent(EventType.CREATE, value));
		}
		return v;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Value> m) {
		if (CollectionUtils.isEmpty(m)) {
			return;
		}

		for (Entry<? extends String, ? extends Value> entry : m.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}

	public PropertiesRegistration loadProperties(String resource) {
		return loadProperties(resource, (String) null);
	}

	public PropertiesRegistration loadProperties(String resource, String charsetName) {
		return loadProperties(null, resource, charsetName);
	}

	public PropertiesRegistration loadProperties(String keyPrefix, String resource, String charsetName) {
		ObservableResource<Properties> res = ResourceUtils.getResourceOperations().getProperties(resource, charsetName);
		if (res.getResource() != null) {
			loadProperties(keyPrefix, res.getResource());
		}

		return new PropertiesRegistration(keyPrefix, res);
	}

	public void loadProperties(Properties properties) {
		loadProperties(null, properties);
	}

	public void loadProperties(String keyPrefix, Properties properties) {
		if (properties == null) {
			return;
		}

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

	public Value put(String key, Object value) {
		return put(key, createValue(value));
	}

	public Value putIfAbsent(String key, Object value) {
		return putIfAbsent(key, createValue(value));
	}

	protected Value createValue(Object value) {
		return value instanceof String ? new StringValue((String) value) : new AnyValue(value);
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

		public DefaultDynamicMap getDefaultDynamicMap() {
			return DefaultDynamicMap.this;
		}

		public DefaultDynamicMap registerListener() {
			if (isRegisterListener()) {
				return getDefaultDynamicMap();
			}

			this.eventRegistration = registerListener(new ObservableResourceEventListener<Properties>() {

				public void onEvent(ObservableResourceEvent<Properties> event) {
					getDefaultDynamicMap().loadProperties(keyPrefix, event.getSource());
				}
			});
			return getDefaultDynamicMap();
		}

		public EventRegistration registerListener(ObservableResourceEventListener<Properties> eventListener) {
			return resource.registerListener(eventListener);
		}

		public void unregister() {
			if (eventRegistration != null) {
				eventRegistration.unregister();
			}
		}
	}
}
