package scw.value.property;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.utils.CollectionUtils;
import scw.event.AbstractObservable;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.event.Observable;
import scw.event.ObservableEvent;
import scw.event.support.StringNamedEventDispatcher;
import scw.io.Resource;
import scw.io.event.ObservableProperties;
import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.Value;

public class PropertiesPropertyFactory extends
		AbstractObservable<Map<String, Value>> implements BasePropertyFactory {
	private final NamedEventDispatcher<String, PropertyEvent> eventDispatcher;
	private final Observable<Properties> observableProperties;
	private final ValueCreator valueCreator;
	private final String keyPrefix;

	public PropertiesPropertyFactory(boolean concurrent,
			ValueCreator valueCreator, String keyPrefix,
			Collection<Resource> resources, String charsetName) {
		this(new ObservableProperties(resources, charsetName), keyPrefix, concurrent, valueCreator);
	}

	public PropertiesPropertyFactory(
			Observable<Properties> observableProperties, String keyPrefix,
			boolean concurrent, ValueCreator valueCreator) {
		this.eventDispatcher = new StringNamedEventDispatcher<PropertyEvent>(
				concurrent);
		this.observableProperties = observableProperties;
		this.keyPrefix = keyPrefix;
		this.valueCreator = valueCreator;
	}

	public Map<String, Value> forceGet() {
		Properties properties = observableProperties.forceGet();
		if (CollectionUtils.isEmpty(properties)) {
			return Collections.emptyMap();
		}

		Map<String, Value> valueMap = new LinkedHashMap<String, Value>(
				properties.size());
		for (Entry<Object, Object> entry : properties.entrySet()) {
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}

			String key = entry.getKey().toString();
			String k = keyPrefix == null ? key : (keyPrefix + key);
			valueMap.put(
					k,
					valueCreator == null ? ValueCreator.CREATOR
							.create(k, value) : valueCreator.create(k, value));
		}
		return Collections.unmodifiableMap(valueMap);
	}

	@Override
	public Map<String, Value> get() {
		Map<String, Value> valueMap = super.get();
		if (valueMap == null) {
			return Collections.emptyMap();
		}
		return valueMap;
	}

	public Value getValue(String key) {
		return get().get(key);
	}

	public Iterator<String> iterator() {
		return get().keySet().iterator();
	}

	public boolean containsKey(String key) {
		return get().containsKey(key);
	}

	public EventRegistration registerListener(String name,
			EventListener<PropertyEvent> eventListener) {
		return eventDispatcher.registerListener(name, eventListener);
	}

	@Override
	public void onEvent(ObservableEvent<Map<String, Value>> event) {
		Map<String, Value> oldValueMap = new LinkedHashMap<String, Value>(get());
		super.onEvent(event);
		for (Entry<String, Value> entry : oldValueMap.entrySet()) {
			eventDispatcher.publishEvent(
					entry.getKey(),
					new PropertyEvent(this, event.getEventType(), entry
							.getKey(), entry.getValue()));
		}
	}

	public EventRegistration registerListener(
			boolean exists,
			final EventListener<ObservableEvent<Map<String, Value>>> eventListener) {
		return observableProperties.registerListener(exists,
				new EventListener<ObservableEvent<Properties>>() {

					public void onEvent(ObservableEvent<Properties> event) {
						eventListener
								.onEvent(new ObservableEvent<Map<String, Value>>(
										event.getEventType(), forceGet()));
					}
				});
	}
	
	public interface ValueCreator {
		static final ValueCreator CREATOR = new ValueCreator() {

			public Value create(String key, Object value) {
				if (value instanceof Value) {
					return (Value) value;
				} else if (value instanceof String) {
					return new StringValue((String) value);
				} else {
					return new AnyValue(value);
				}
			}
		};

		Value create(String key, Object value);
	}
}
