package scw.env.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import scw.core.Assert;
import scw.env.PropertyManager;
import scw.env.support.ObservablePropertiesPropertyFactory.ValueCreator;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.KeyValuePairEvent;
import scw.event.MultiEventRegistration;
import scw.event.Observable;
import scw.event.support.ObservableMap;
import scw.event.support.StringNamedEventDispatcher;
import scw.util.CollectionFactory;
import scw.util.MultiIterator;
import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.Value;
import scw.value.ValueWrapper;
import scw.value.factory.ListenablePropertyFactory;
import scw.value.factory.PropertyFactory;
import scw.value.factory.support.AbstractObservablePropertyFactory;

public class DefaultPropertyManager extends AbstractObservablePropertyFactory implements PropertyManager{
	private final ObservableMap<String, Value> propertyMap;
	private final List<PropertyFactory> propertyFactories;
	
	/**
	 * @param concurrent
	 * @param priorityOfUseSelf
	 *            是否优先使用自身的值
	 * @param properties解析器
	 */
	public DefaultPropertyManager(boolean concurrent) {
		this.propertyMap = new ObservableMap<String, Value>(
				concurrent,
				new StringNamedEventDispatcher<KeyValuePairEvent<String, Value>>(
						concurrent));
		this.propertyFactories = CollectionFactory.createArrayList(concurrent,
				8);
	}
	
	public void addPropertyFactory(PropertyFactory propertyFactory) {
		if (propertyFactory == null) {
			return;
		}

		propertyFactories.add(propertyFactory);
	}

	public List<PropertyFactory> getPropertyFactories() {
		return Collections.unmodifiableList(propertyFactories);
	}

	public Value getValue(String key) {
		Value value = propertyMap.get(key);
		if (value != null) {
			return value;
		}

		for (PropertyFactory propertyFactory : propertyFactories) {
			value = propertyFactory.getValue(key);
			if (value != null) {
				return value;
			}
		}
		return null;
	}
	
	public Iterator<String> iterator() {
		List<Iterator<String>> iterators = new LinkedList<Iterator<String>>();
		iterators.add(propertyMap.keySet().iterator());
		for (PropertyFactory propertyFactory : propertyFactories) {
			iterators.add(propertyFactory.iterator());
		}
		return new MultiIterator<String>(iterators);
	}

	public boolean containsKey(String key) {
		if (propertyMap.containsKey(key)) {
			return true;
		}

		for (PropertyFactory basePropertyFactory : propertyFactories) {
			if (basePropertyFactory.containsKey(key)) {
				return true;
			}
		}
		return false;
	}

	public EventRegistration registerListener(final String key,
			final EventListener<ChangeEvent<String>> eventListener) {
		EventRegistration registration1 = this.propertyMap.getEventDispatcher()
				.registerListener(key,
						new EventListener<KeyValuePairEvent<String, Value>>() {

							public void onEvent(
									KeyValuePairEvent<String, Value> event) {
								eventListener.onEvent(new ChangeEvent<String>(
										event, event.getSource().getKey()));
							}
						});

		if (propertyFactories.size() == 0) {
			return registration1;
		}

		List<EventRegistration> registrations = new ArrayList<EventRegistration>(
				propertyFactories.size());
		registrations.add(registration1);
		for (PropertyFactory propertyFactory : propertyFactories) {
			if (propertyFactory instanceof ListenablePropertyFactory) {
				EventRegistration registration = ((ListenablePropertyFactory) propertyFactory)
						.registerListener(key, eventListener);
				registrations.add(registration);
			}
		}
		return new MultiEventRegistration(
				registrations.toArray(new EventRegistration[0]));
	}

	public boolean remove(String key) {
		Assert.requiredArgument(key != null, "key");
		Value value = propertyMap.remove(key);
		return value != null;
	}

	public boolean put(String key, Value value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		propertyMap.put(key, value);
		return true;
	}
	
	public boolean putIfAbsent(String key, Value value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return propertyMap.putIfAbsent(key, value) == null;
	}

	public boolean put(String key, Object value) {
		return put(key, value, true);
	}

	public boolean put(String key, Object value, boolean format) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return put(key, toValue(value, format));
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

	public boolean putIfAbsent(String key, Object value) {
		return putIfAbsent(key, value, false) == null;
	}

	public Value putIfAbsent(String key, Object value, boolean format) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return propertyMap.putIfAbsent(key, toValue(value, format));
	}

	public void clear() {
		propertyMap.clear();
	}

	public final Observable<Map<String, Value>> loadProperties(
			Observable<Properties> properties) {
		return loadProperties(null, properties);
	}

	public final Observable<Map<String, Value>> loadProperties(String prefix,
			Observable<Properties> properties) {
		return loadProperties(prefix, properties, true);
	}

	public final Observable<Map<String, Value>> loadProperties(String keyPrefix,
			Observable<Properties> properties,
			final boolean format) {
		ValueCreator valueCreator = new ValueCreator() {

			public Value create(String key, Object value) {
				return toValue(value, format);
			}
		};

		ObservablePropertiesPropertyFactory factory = new ObservablePropertiesPropertyFactory(
				properties, keyPrefix, propertyMap.isConcurrent(), valueCreator);
		addPropertyFactory(factory);
		return factory;
	}

	private class StringFormatValue extends StringValue {
		private static final long serialVersionUID = 1L;

		public StringFormatValue(String value) {
			super(value);
		}

		@Override
		public String getAsString() {
			return resolvePlaceholders(super.getAsString());
		}
	}

	private class AnyFormatValue extends AnyValue {

		public AnyFormatValue(Object value) {
			super(value);
		}

		public String getAsString() {
			return resolvePlaceholders(super.getAsString());
		};
	}

	private class FormatValue extends ValueWrapper {

		public FormatValue(Value value) {
			super(value);
		}

		@Override
		public String getAsString() {
			return resolvePlaceholders(super.getAsString());
		}
	}
}
