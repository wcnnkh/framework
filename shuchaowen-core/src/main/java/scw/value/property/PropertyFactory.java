package scw.value.property;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;

import scw.core.Assert;
import scw.core.utils.CollectionUtils;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.method.MultiEventRegistration;
import scw.event.support.ValueEvent;
import scw.util.MultiEnumeration;
import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.StringValueFactory;
import scw.value.Value;
import scw.value.ValueWrapper;
import scw.value.property.DynamicMap.DynamicMapRegistration;
import scw.value.property.DynamicMap.ValueCreator;

public class PropertyFactory extends StringValueFactory implements BasePropertyFactory {
	private final List<BasePropertyFactory> basePropertyFactories;
	private final DynamicMap dynamicMap;
	private final boolean priorityOfUseSelf;

	/**
	 * @param concurrent
	 * @param priorityOfUseSelf
	 *            是否优先使用自身的值
	 */
	public PropertyFactory(boolean concurrent, boolean priorityOfUseSelf) {
		this.dynamicMap = new DynamicMap(concurrent);
		this.basePropertyFactories = concurrent ? new CopyOnWriteArrayList<BasePropertyFactory>()
				: new LinkedList<BasePropertyFactory>();
		this.priorityOfUseSelf = priorityOfUseSelf;
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
			Value value = dynamicMap.get(key);
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
			value = dynamicMap.get(key);
		}
		return value;
	}

	public Enumeration<String> enumerationKeys() {
		List<Enumeration<String>> enumerations = new LinkedList<Enumeration<String>>();
		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			enumerations.add(basePropertyFactory.enumerationKeys());
		}
		enumerations.add(Collections.enumeration(dynamicMap.keySet()));
		return new MultiEnumeration<String>(enumerations);
	}

	public boolean containsKey(String key) {
		if (dynamicMap.containsKey(key)) {
			return true;
		}

		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			if (basePropertyFactory.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	private class PropertyEventListener implements EventListener<ValueEvent<Value>> {
		private final EventListener<PropertyEvent> eventListener;
		private final String key;

		public PropertyEventListener(String key, EventListener<PropertyEvent> eventListener) {
			this.key = key;
			this.eventListener = eventListener;
		}

		public void onEvent(ValueEvent<Value> event) {
			eventListener.onEvent(new PropertyEvent(PropertyFactory.this, key, event));
		}
	}

	public EventRegistration registerListener(String key, EventListener<PropertyEvent> eventListener) {
		EventRegistration registration = dynamicMap.getEventDispatcher().registerListener(key,
				new PropertyEventListener(key.toString(), eventListener));
		EventRegistration[] registrations = new EventRegistration[basePropertyFactories.size() + 1];
		registrations[0] = registration;
		int index = 1;
		for (BasePropertyFactory basePropertyFactory : basePropertyFactories) {
			registrations[index++] = basePropertyFactory.registerListener(key, eventListener);
		}
		return new MultiEventRegistration(registrations);
	}

	public Value remove(String key) {
		Assert.requiredArgument(key != null, "key");
		return dynamicMap.remove(key);
	}

	public Value put(String key, Object value) {
		return put(key, value, false);
	}

	public Value put(String key, Object value, boolean format) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return dynamicMap.put(key, toValue(value, format));
	}

	private Value toValue(Object value, boolean format) {
		Value v;
		if (value instanceof Value) {
			if (value instanceof StringFormatValue || value instanceof AnyFormatValue || value instanceof FormatValue) {
				v = (Value) value;
			} else {
				v = format ? new FormatValue((Value) value) : (Value) value;
			}
		} else if (value instanceof String) {
			v = format ? new StringFormatValue((String) value) : new StringValue((String) value);
		} else {
			v = format ? new AnyFormatValue(value) : new AnyValue(value);
		}
		return v;
	}

	public Value putIfAbsent(String key, Object value) {
		return putIfAbsent(key, value, false);
	}

	public Value putIfAbsent(String key, Object value, boolean format) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return dynamicMap.putIfAbsent(key, toValue(value, format));
	}

	public void clear() {
		dynamicMap.clear();
	}

	public PropertyFactoryRegistration loadProperties(String resource) {
		return loadProperties(resource, (String) null);
	}

	public PropertyFactoryRegistration loadProperties(String resource, String charsetName) {
		return loadProperties(null, resource, charsetName);
	}

	public PropertyFactoryRegistration loadProperties(final String keyPrefix, String resource, String charsetName) {
		return loadProperties(keyPrefix, resource, charsetName, false);
	}

	public PropertyFactoryRegistration loadProperties(String resource, boolean format) {
		return loadProperties((String) resource, (String) null, format);
	}

	public PropertyFactoryRegistration loadProperties(String resource, String charsetName, boolean format) {
		return loadProperties(null, resource, charsetName, format);
	}

	public PropertyFactoryRegistration loadProperties(final String keyPrefix, String resource, String charsetName,
			final boolean format) {
		DynamicMapRegistration propertiesRegistration = dynamicMap.loadProperties(keyPrefix, resource, charsetName,
				new ValueCreator() {

					public Value create(String key, Object value) {
						return toValue(value, format);
					}
				});
		return new PropertyFactoryRegistration(propertiesRegistration);
	}

	public void loadProperties(Properties properties) {
		loadProperties(null, properties, false);
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

	public <T> DynamicValue<T> getDynamicValue(String name, Class<? extends T> type, T defaultValue) {
		return new DynamicValue<T>(name, type, defaultValue);
	}

	public DynamicValue<Object> getDynamicValue(String name, Type type, Object defaultValue) {
		return new DynamicValue<Object>(name, type, defaultValue);
	}

	public class PropertyFactoryRegistration {
		private final DynamicMapRegistration propertiesRegistration;

		public PropertyFactoryRegistration(DynamicMapRegistration propertiesRegistration) {
			this.propertiesRegistration = propertiesRegistration;
		}

		public PropertyFactory getPropertyFactory() {
			return PropertyFactory.this;
		}

		public DynamicMapRegistration getPropertiesRegistration() {
			return propertiesRegistration;
		}

		/**
		 * 默认只有当资源存在时才注册
		 * 
		 * @return
		 */
		public PropertyFactory registerListener() {
			return registerListener(true);
		}

		/**
		 * @param isExist
		 *            true表示只有当资源存在时才注册
		 * @return
		 */
		public PropertyFactory registerListener(boolean isExist) {
			propertiesRegistration.register(isExist);
			return getPropertyFactory();
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

	class AnyFormatValue extends AnyValue {
		private static final long serialVersionUID = 1L;

		public AnyFormatValue(Object value) {
			super(value);
		}

		public String getAsString() {
			String value = super.getAsString();
			return format(value, true);
		};
	}

	class FormatValue extends ValueWrapper {

		public FormatValue(Value value) {
			super(value);
		}

		@Override
		public String getAsString() {
			String value = super.getAsString();
			return format(value, true);
		}
	}

	public class DynamicValue<T> {
		private final String name;
		private volatile T value;
		private final T defaultValue;
		private final Type type;
		private EventRegistration eventRegistration;

		public DynamicValue(String name, Type type, T defaultValue) {
			this.name = name;
			this.type = type;
			this.defaultValue = defaultValue;
			this.value = this.parse(PropertyFactory.this.get(name));

			eventRegistration = registerListener(new EventListener<ValueEvent<T>>() {

				public void onEvent(ValueEvent<T> event) {
					setValue(event.getValue());
				}
			});
		}

		@Override
		protected void finalize() throws Throwable {
			if (eventRegistration != null) {
				eventRegistration.unregister();
			}
			super.finalize();
		}

		public T getValue() {
			return value;
		}

		protected void setValue(T value) {
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public T getDefaultValue() {
			return defaultValue;
		}

		public Type getType() {
			return type;
		}

		@SuppressWarnings("unchecked")
		protected T parse(Value value) {
			return (T) (value == null ? this.getDefaultValue() : value.getAsObject(getType()));
		}

		public EventRegistration registerListener(final EventListener<ValueEvent<T>> eventListener) {
			return PropertyFactory.this.registerListener(getName(), new EventListener<PropertyEvent>() {

				public void onEvent(PropertyEvent event) {
					T value = parse(event.getValue());
					eventListener.onEvent(new ValueEvent<T>(event, value));
				}
			});
		}
	}
}
