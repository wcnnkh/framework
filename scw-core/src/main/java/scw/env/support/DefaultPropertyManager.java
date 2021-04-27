package scw.env.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import scw.convert.ConversionService;
import scw.core.Assert;
import scw.core.utils.CollectionUtils;
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
import scw.value.AbstractPropertyFactory;
import scw.value.AnyValue;
import scw.value.ListenablePropertyFactory;
import scw.value.PropertyFactory;
import scw.value.StringValue;
import scw.value.Value;

public class DefaultPropertyManager extends AbstractPropertyFactory implements PropertyManager{
	private final ObservableMap<String, Value> propertyMap;
	private final List<PropertyFactory> propertyFactories;
	private ConversionService conversionService;
	
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
	
	protected void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	
	public void addPropertyFactory(PropertyFactory propertyFactory) {
		if (propertyFactory == null) {
			return;
		}

		propertyFactories.add(propertyFactory);
	}
	
	protected Iterator<PropertyFactory> getPropertyFactoriesIterator(){
		return CollectionUtils.getIterator(propertyFactories, true);
	}
	
	public List<PropertyFactory> getPropertyFactories() {
		return Collections.unmodifiableList(propertyFactories);
	}

	public Value getValue(String key) {
		Value value = propertyMap.get(key);
		if (value != null) {
			return value;
		}

		Iterator<PropertyFactory> iterator = getPropertyFactoriesIterator();
		while(iterator.hasNext()){
			value = iterator.next().getValue(key);
			if (value != null) {
				return value;
			}
		}
		return null;
	}
	
	public Iterator<String> iterator() {
		List<Iterator<String>> iterators = new LinkedList<Iterator<String>>();
		iterators.add(propertyMap.keySet().iterator());
		Iterator<PropertyFactory> iterator = getPropertyFactoriesIterator();
		while(iterator.hasNext()){
			iterators.add(iterator.next().iterator());
		}
		return new MultiIterator<String>(iterators);
	}

	public boolean containsKey(String key) {
		if (propertyMap.containsKey(key)) {
			return true;
		}

		Iterator<PropertyFactory> iterator = getPropertyFactoriesIterator();
		while(iterator.hasNext()){
			if(iterator.next().containsKey(key)){
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
		Iterator<PropertyFactory> iterator = getPropertyFactoriesIterator();
		while(iterator.hasNext()){
			PropertyFactory propertyFactory = iterator.next();
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
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return put(key, toProperty(value));
	}

	public Value toProperty(Object value) {
		Value v;
		if (value instanceof Value) {
			return (Value) value;
		} else if (value instanceof String) {
			v = new StringFormatValue((String) value);
		} else {
			v = new AnyFormatValue(value);
		}
		return v;
	}

	public boolean putIfAbsent(String key, Object value) {
		Assert.requiredArgument(key != null, "key");
		Assert.requiredArgument(value != null, "value");
		return propertyMap.putIfAbsent(key, toProperty(value)) == null;
	}

	public void clear() {
		propertyMap.clear();
	}

	public final Observable<Map<String, Value>> loadProperties(
			Observable<Properties> properties) {
		return loadProperties(null, properties);
	}

	public final Observable<Map<String, Value>> loadProperties(String keyPrefix,
			Observable<Properties> properties) {
		ValueCreator valueCreator = new ValueCreator() {

			public Value create(String key, Object value) {
				return toProperty(value);
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
		private static final long serialVersionUID = 1L;

		public AnyFormatValue(Object value) {
			super(value, conversionService);
		}

		public String getAsString() {
			return resolvePlaceholders(super.getAsString());
		};
	}
}
