package scw.io.event;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import scw.convert.EmptyConverter;
import scw.convert.lang.ObjectToStringConverter;
import scw.core.IteratorConverter;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.Observable;
import scw.value.AnyValue;
import scw.value.PropertyFactory;
import scw.value.Value;

public class ObservableProperties extends ConvertibleObservableProperties<Properties> implements PropertyFactory {

	public ObservableProperties() {
		super(new EmptyConverter<Properties>());
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
		return new IteratorConverter<Object, String>(get().keySet().iterator(), new ObjectToStringConverter());
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
