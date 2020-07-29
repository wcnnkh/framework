package scw.value.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
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

	public boolean isSupportedConcurrent() {
		return getTargetMap() instanceof ConcurrentMap;
	}
	
	@Override
	public Set<java.util.Map.Entry<String, Value>> entrySet() {
		return Collections.unmodifiableSet(super.entrySet());
	}
	
	@Override
	public Set<String> keySet() {
		return Collections.unmodifiableSet(super.keySet());
	}
	
	@Override
	public Collection<Value> values() {
		return Collections.unmodifiableCollection(super.values());
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

	public DynamicMapRegistration loadProperties(String resource, ValueCreator creator) {
		return loadProperties(resource, (String) null, creator);
	}

	public DynamicMapRegistration loadProperties(String resource, String charsetName, ValueCreator creator) {
		return loadProperties(null, resource, charsetName, creator);
	}

	public DynamicMapRegistration loadProperties(String keyPrefix, String resource, String charsetName,
			ValueCreator creator) {
		ObservableResource<Properties> res = ResourceUtils.getResourceOperations().getProperties(resource, charsetName);
		if (res.getResource() != null) {
			loadProperties(keyPrefix, res.getResource(), creator);
		}

		return new DefaultPropertiesRegistration(keyPrefix, res, creator);
	}

	public void loadProperties(Properties properties, ValueCreator creator) {
		loadProperties(null, properties, creator);
	}

	public void loadProperties(String keyPrefix, Properties properties, ValueCreator creator) {
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

			put(keyPrefix == null ? key.toString() : (keyPrefix + key.toString()),
					creator.create(key.toString(), value));
		}
	}

	private class DefaultPropertiesRegistration implements DynamicMapRegistration {
		private final ObservableResource<Properties> resource;
		private volatile EventRegistration eventRegistration;
		private final String keyPrefix;
		private final ValueCreator creator;
		private final List<String> keys = new ArrayList<String>();

		public DefaultPropertiesRegistration(String keyPrefix, ObservableResource<Properties> resource,
				ValueCreator creator) {
			this.keyPrefix = keyPrefix;
			this.resource = resource;
			this.creator = creator;
			addKeys(resource.getResource());
		}

		private void addKeys(Properties properties) {
			if (properties == null) {
				return;
			}

			for (Object key : properties.keySet()) {
				if (key == null) {
					continue;
				}

				keys.add(key.toString());
			}
		}

		public boolean isRegister() {
			return eventRegistration != null;
		}

		public void register() {
			if (isRegister()) {
				return;
			}
			
			synchronized (this) {
				if(isRegister()){
					return ;
				}
				
				this.eventRegistration = resource.registerListener(new ObservableResourceEventListener<Properties>() {

					public void onEvent(ObservableResourceEvent<Properties> event) {
						Properties properties = event.getSource();
						HashSet<String> keySet = new HashSet<String>(keys);
						if (properties != null) {
							for (Entry<Object, Object> entry : properties.entrySet()) {
								Object value = entry.getValue();
								if (value == null) {
									continue;
								}

								String key = entry.getKey().toString();
								String k = keyPrefix == null ? key : (keyPrefix + key);
								Value v = creator.create(key, value);
								put(k, v);
								keySet.remove(key);
							}
						}
						for (String key : keySet) {
							String k = keyPrefix == null ? key : (keyPrefix + key);
							remove(k);
						}
						keys.clear();
						addKeys(properties);
					}
				});
			}
		}

		public void unregister() {
			if (eventRegistration != null) {
				synchronized (this) {
					eventRegistration.unregister();
				}
			}
			eventRegistration = null;
		}
	}
}
