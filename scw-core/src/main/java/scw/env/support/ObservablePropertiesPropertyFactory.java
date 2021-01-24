package scw.env.support;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import scw.core.utils.CollectionUtils;
import scw.event.ChangeEvent;
import scw.event.ConvertibleObservable;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.event.Observable;
import scw.event.support.StringNamedEventDispatcher;
import scw.value.AnyValue;
import scw.value.StringValue;
import scw.value.Value;
import scw.value.factory.ListenablePropertyFactory;

public class ObservablePropertiesPropertyFactory extends
		ConvertibleObservable<Properties, Map<String, Value>> implements ListenablePropertyFactory {
	private final NamedEventDispatcher<String, ChangeEvent<String>> eventDispatcher;
	private final ValueCreator valueCreator;
	private final String keyPrefix;

	public ObservablePropertiesPropertyFactory(
			Observable<Properties> properties, String keyPrefix,
			boolean concurrent, ValueCreator valueCreator) {
		super(properties);
		this.eventDispatcher = new StringNamedEventDispatcher<ChangeEvent<String>>(
				concurrent);
		this.keyPrefix = keyPrefix;
		this.valueCreator = valueCreator;
	}

	public Map<String, Value> convert(Properties properties) {
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
			EventListener<ChangeEvent<String>> eventListener) {
		return eventDispatcher.registerListener(name, eventListener);
	}

	@Override
	public void onEvent(ChangeEvent<Map<String, Value>> event) {
		Map<String, Value> oldValueMap = new LinkedHashMap<String, Value>(get());
		super.onEvent(event);
		for (Entry<String, Value> entry : oldValueMap.entrySet()) {
			eventDispatcher.publishEvent(entry.getKey(), new ChangeEvent<String>(event, entry.getKey()));
		}
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
