package io.basc.framework.io.event;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.function.Function;

import io.basc.framework.convert.ConvertibleIterator;
import io.basc.framework.convert.lang.ObjectToStringConverter;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.event.Observable;
import io.basc.framework.value.AnyValue;
import io.basc.framework.value.PropertyFactory;
import io.basc.framework.value.Value;

public class ObservableProperties extends ConvertibleObservableProperties<Properties> implements PropertyFactory {

	public ObservableProperties() {
		super(Function.identity());
	}

	@SafeVarargs
	public ObservableProperties(Observable<Properties>... observables) {
		this();
		for (Observable<Properties> observable : observables) {
			combine(observable);
		}
	}

	@Override
	public Iterator<String> iterator() {
		return new ConvertibleIterator<Object, String>(get().keySet().iterator(), new ObjectToStringConverter());
	}

	@Override
	public Value getValue(String key) {
		Object value = get().get(key);
		return value == null ? null : new AnyValue(value);
	}

	@Override
	public EventRegistration registerListener(String name, EventListener<ChangeEvent<String>> eventListener) {
		return registerListener(new EventListener<ChangeEvent<Properties>>() {

			@Override
			public void onEvent(ChangeEvent<Properties> event) {
				for (Entry<Object, Object> entry : event.getSource().entrySet()) {
					eventListener.onEvent(new ChangeEvent<String>(event, String.valueOf(entry.getKey())));
				}
			}
		});
	}

}
