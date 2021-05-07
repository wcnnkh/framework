package scw.value;

import java.lang.reflect.Type;

import scw.event.AbstractObservable;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;

public class ObservableValue<K, V> extends AbstractObservable<V> {
	private final ListenableValueFactory<K> valueFactory;
	private final K name;
	private final V defaultValue;
	private final Type type;

	public ObservableValue(final ListenableValueFactory<K> valueFactory,
			K name, Type type, V defaultValue) {
		this.valueFactory = valueFactory;
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		setRegisterOnlyExists(false);
		register();
	}

	@SuppressWarnings("unchecked")
	public V forceGet() {
		return (V) valueFactory.getValue(name, type, defaultValue);
	}

	public EventRegistration registerListener(boolean exists,
			final EventListener<ChangeEvent<V>> eventListener) {
		if (exists && !valueFactory.containsKey(name)) {
			return EventRegistration.EMPTY;
		}

		return valueFactory.registerListener(name,
				new EventListener<ChangeEvent<K>>() {
					public void onEvent(ChangeEvent<K> event) {
						eventListener.onEvent(new ChangeEvent<V>(event,
								forceGet()));
					}
				});
	}
}
