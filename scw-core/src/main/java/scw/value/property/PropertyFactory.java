package scw.value.property;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.Assert;
import scw.core.utils.CollectionUtils;
import scw.event.AbstractObservable;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.KeyValuePairEvent;
import scw.event.MultiEventRegistration;
import scw.event.Observable;
import scw.event.support.ObservableMap;
import scw.event.support.StringNamedEventDispatcher;
import scw.io.ResourceUtils;
import scw.util.CollectionFactory;
import scw.util.MultiIterator;
import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.StringValueFactory;
import scw.value.Value;
import scw.value.ValueWrapper;
import scw.value.property.PropertiesPropertyFactory.ValueCreator;

public class PropertyFactory extends StringValueFactory implements EditablePropertyFactory {
	private final ObservableMap<String, Value> propertyMap;
	private final List<BasePropertyFactory> basePropertyFactories;
	private final boolean priorityOfUseSelf;

	/**
	 * @param concurrent
	 * @param priorityOfUseSelf
	 *            是否优先使用自身的值
	 */
	public PropertyFactory(boolean concurrent, boolean priorityOfUseSelf) {
		this.propertyMap = new ObservableMap<String, Value>(concurrent, new StringNamedEventDispatcher<KeyValuePairEvent<String, Value>>(concurrent));
		this.basePropertyFactories = CollectionFactory.createArrayList(concurrent, 8);
		this.priorityOfUseSelf = priorityOfUseSelf;
	}
	
	public boolean isPriorityOfUseSelf() {
		return priorityOfUseSelf;
	}
	
	public void addFirstBasePropertyFactory(
			BasePropertyFactory basePropertyFactory) {
		if (basePropertyFactory == null) {
			return;
		}

		basePropertyFactories.add(0, basePropertyFactory);
	}

	public void addLastBasePropertyFactory(
			BasePropertyFactory basePropertyFactory) {
		if (basePropertyFactory == null) {
			return;
		}

		basePropertyFactories.add(basePropertyFactory);
	}

	public void addLastBasePropertyFactory(
			List<BasePropertyFactory> basePropertyFactories) {
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
	public Value getValue(String key) {
		if (priorityOfUseSelf) {
			Value value = propertyMap.get(key);
			if (value != null) {
				return value;
			}
		}

		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			Value value = basePropertyFactory.getValue(key);
			if (value != null) {
				return value;
			}
		}

		Value value = super.getValue(key);
		if (value == null && !priorityOfUseSelf) {
			value = propertyMap.get(key);
		}
		return value;
	}

	public Iterator<String> iterator() {
		List<Iterator<String>> iterators = new LinkedList<Iterator<String>>();
		if (priorityOfUseSelf) {
			iterators.add(propertyMap.keySet().iterator());
		}

		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			iterators.add(basePropertyFactory.iterator());
		}

		if (!priorityOfUseSelf) {
			iterators.add(propertyMap.keySet().iterator());
		}

		return new MultiIterator<String>(iterators);
	}

	public boolean containsKey(String key) {
		if (propertyMap.containsKey(key)) {
			return true;
		}

		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			if (basePropertyFactory.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public EventRegistration registerListener(String key,
			final EventListener<PropertyEvent> eventListener) {
		EventRegistration registration1 = this.propertyMap.getEventDispatcher().registerListener(key,  new EventListener<KeyValuePairEvent<String,Value>>() {

			public void onEvent(KeyValuePairEvent<String, Value> event) {
				eventListener.onEvent(new PropertyEvent(PropertyFactory.this, event));
			}
		});
		
		EventRegistration registration2 = MultiEventRegistration.registerListener(key, eventListener, getBasePropertyFactories());
		return new MultiEventRegistration(registration1, registration2);
	}
	
	public boolean put(String key, String value) {
		// TODO Auto-generated method stub
		return false;
	} 

	public boolean remove(String key) {
		Assert.requiredArgument(key != null, "key");
		Value value = propertyMap.remove(key);
		return value != null;
	}

	public boolean put(String key, Object value) {
		Value v = put(key, value, false);
		return v != null;
	}

	public Value put(String key, Object value, boolean format) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return propertyMap.put(key, toValue(value, format));
	}

	private Value toValue(Object value, boolean resolvePlaceholders) {
		Value v;
		if (value instanceof Value) {
			if (value instanceof StringFormatValue
					|| value instanceof AnyFormatValue
					|| value instanceof FormatValue) {
				v = (Value) value;
			} else {
				v = resolvePlaceholders ? new FormatValue((Value) value)
						: (Value) value;
			}
		} else if (value instanceof String) {
			v = resolvePlaceholders ? new StringFormatValue((String) value)
					: new StringValue((String) value);
		} else {
			v = resolvePlaceholders ? new AnyFormatValue(value) : new AnyValue(
					value);
		}
		return v;
	}

	public Value putIfAbsent(String key, Object value) {
		return putIfAbsent(key, value, false);
	}

	public Value putIfAbsent(String key, Object value, boolean format) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return propertyMap.putIfAbsent(key, toValue(value, format));
	}

	public void clear() {
		propertyMap.clear();
	}

	public Observable<Map<String, Value>> loadProperties(String resource) {
		return loadProperties(resource, (String) null);
	}

	public Observable<Map<String, Value>> loadProperties(String resource,
			String charsetName) {
		return loadProperties(null, resource, charsetName);
	}

	public Observable<Map<String, Value>> loadProperties(
			final String keyPrefix, String resource, String charsetName) {
		return loadProperties(keyPrefix, resource, charsetName, true);
	}

	public Observable<Map<String, Value>> loadProperties(String resource,
			boolean format) {
		return loadProperties((String) resource, (String) null, format);
	}

	public Observable<Map<String, Value>> loadProperties(String resource,
			String charsetName, boolean format) {
		return loadProperties(null, resource, charsetName, format);
	}

	public Observable<Map<String, Value>> loadProperties(
			final String keyPrefix, String resource, String charsetName,
			final boolean format) {
		return loadProperties(ResourceUtils.getResourceOperations()
				.getProperties(resource, charsetName), keyPrefix, format);
	}

	public Observable<Map<String, Value>> loadProperties(
			Observable<Properties> properties) {
		return loadProperties(properties, true);
	}

	public Observable<Map<String, Value>> loadProperties(
			Observable<Properties> properties, boolean format) {
		return loadProperties(properties, null, format);
	}

	public Observable<Map<String, Value>> loadProperties(
			Observable<Properties> properties, String keyPrefix,
			final boolean format) {
		ValueCreator valueCreator = new ValueCreator() {

			public Value create(String key, Object value) {
				return toValue(value, format);
			}
		};

		PropertiesPropertyFactory factory = new PropertiesPropertyFactory(
				properties, keyPrefix, propertyMap.isConcurrent(), valueCreator);
		addFirstBasePropertyFactory(factory);
		return factory;
	}

	public void loadProperties(Properties properties) {
		loadProperties(null, properties, false);
	}

	public void loadProperties(String keyPrefix, Properties properties) {
		loadProperties(keyPrefix, properties, false);
	}

	public void loadProperties(String keyPrefix, Properties properties,
			boolean format) {
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
				put(keyPrefix == null ? key.toString()
						: (keyPrefix + key.toString()), value, format);
			}
		}
	}

	public final <T> Observable<T> getObservableValue(String name,
			Class<? extends T> type, T defaultValue) {
		return new ObservableProperty<T>(name, type, defaultValue);
	}

	public final Observable<Object> getObservableValue(String name, Type type,
			Object defaultValue) {
		return new ObservableProperty<Object>(name, type, defaultValue);
	}

	private class StringFormatValue extends StringValue {
		private static final long serialVersionUID = 1L;

		public StringFormatValue(String value) {
			super(value);
		}

		@Override
		public String getAsString() {
			return format(super.getAsString());
		}
	}

	private class AnyFormatValue extends AnyValue {

		public AnyFormatValue(Object value) {
			super(value);
		}

		public String getAsString() {
			return format(super.getAsString());
		};
	}

	private class FormatValue extends ValueWrapper {

		public FormatValue(Value value) {
			super(value);
		}

		@Override
		public String getAsString() {
			return format(super.getAsString());
		}
	}

	private final class ObservableProperty<T> extends AbstractObservable<T> {
		private final String name;
		private final T defaultValue;
		private final Type type;

		public ObservableProperty(String name, Type type, T defaultValue) {
			this.name = name;
			this.type = type;
			this.defaultValue = defaultValue;
			setRegisterOnlyExists(false);
			register();
		}

		@SuppressWarnings("unchecked")
		public T forceGet() {
			return (T) PropertyFactory.this.getValue(name, type, defaultValue);
		}

		public EventRegistration registerListener(boolean exists,
				final EventListener<ChangeEvent<T>> eventListener) {
			if (exists && !containsKey(name)) {
				return EventRegistration.EMPTY;
			}

			return PropertyFactory.this.registerListener(name,
					new EventListener<PropertyEvent>() {
						public void onEvent(PropertyEvent event) {
							eventListener.onEvent(new ChangeEvent<T>(event, forceGet()));
						}
					});
		}
	}
}
