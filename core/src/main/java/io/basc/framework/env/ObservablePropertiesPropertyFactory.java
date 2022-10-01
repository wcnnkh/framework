package io.basc.framework.env;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.ConvertibleObservable;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.NamedEventDispatcher;
import io.basc.framework.event.Observable;
import io.basc.framework.event.support.SimpleStringNamedEventDispatcher;
import io.basc.framework.util.CollectionUtils;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

public class ObservablePropertiesPropertyFactory extends ConvertibleObservable<Properties, Map<String, Value>>
		implements PropertyFactory {
	private final NamedEventDispatcher<String, ChangeEvent<String>> dispatcher = new SimpleStringNamedEventDispatcher<ChangeEvent<String>>();
	private final EventRegistration eventRegistration;

	public ObservablePropertiesPropertyFactory(Observable<Properties> observableProperties, String keyPrefix,
			PropertyWrapper valueCreator) {
		super(observableProperties, (properties) -> {
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
				valueMap.put(k,
						valueCreator == null ? PropertyWrapper.CREATOR.wrap(k, value) : valueCreator.wrap(k, value));
			}
			return Collections.unmodifiableMap(valueMap);
		});
		eventRegistration = registerListener((event) -> {
			for (Entry<String, Value> entry : event.getSource().entrySet()) {
				dispatcher.publishEvent(entry.getKey(), new ChangeEvent<String>(event, entry.getKey()));
			}
		});
	}

	@Override
	public void close() {
		eventRegistration.unregister();
		super.close();
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
}
