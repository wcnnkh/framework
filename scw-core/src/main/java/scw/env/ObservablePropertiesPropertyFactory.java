package scw.env;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import scw.convert.Converter;
import scw.core.utils.CollectionUtils;
import scw.event.ChangeEvent;
import scw.event.ConvertibleObservable;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.NamedEventDispatcher;
import scw.event.Observable;
import scw.event.support.StringNamedEventDispatcher;
import scw.value.AnyValue;
import scw.value.PropertyFactory;
import scw.value.StringValue;
import scw.value.Value;

public class ObservablePropertiesPropertyFactory extends ConvertibleObservable<Properties, Map<String, Value>>
		implements PropertyFactory {
	private final NamedEventDispatcher<String, ChangeEvent<String>> dispatcher = new StringNamedEventDispatcher<ChangeEvent<String>>(
			true);
	private final EventRegistration eventRegistration;

	public ObservablePropertiesPropertyFactory(Observable<Properties> properties, String keyPrefix,
			ValueCreator valueCreator) {
		super(properties, new Converter<Properties, Map<String, Value>>() {

			@Override
			public Map<String, Value> convert(Properties properties) {
				if (CollectionUtils.isEmpty(properties)) {
					return Collections.emptyMap();
				}

				Map<String, Value> valueMap = new LinkedHashMap<String, Value>(properties.size());
				for (Entry<Object, Object> entry : properties.entrySet()) {
					Object value = entry.getValue();
					if (value == null) {
						continue;
					}

					String key = entry.getKey().toString();
					String k = keyPrefix == null ? key : (keyPrefix + key);
					valueMap.put(k, valueCreator == null ? ValueCreator.CREATOR.create(k, value)
							: valueCreator.create(k, value));
				}
				return Collections.unmodifiableMap(valueMap);
			}
		});
		eventRegistration = registerListener(new EventListener<ChangeEvent<Map<String, Value>>>() {

			@Override
			public void onEvent(ChangeEvent<Map<String, Value>> event) {
				for (Entry<String, Value> entry : event.getSource().entrySet()) {
					dispatcher.publishEvent(entry.getKey(), new ChangeEvent<String>(event, entry.getKey()));
				}
			}
		});
	}
	
	@Override
	protected void finalize() throws Throwable {
		eventRegistration.unregister();
		super.finalize();
	}

	@Override
	public Map<String, Value> get() {
		Map<String, Value> valueMap = super.get();
		if (valueMap == null) {
			return Collections.emptyMap();
		}
		return Collections.unmodifiableMap(valueMap);
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

	public EventRegistration registerListener(String name, EventListener<ChangeEvent<String>> eventListener) {
		return dispatcher.registerListener(name, eventListener);
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
