package scw.value;

import java.lang.reflect.Type;

import scw.event.AbstractObservable;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;

public class ObservableValue<K, V> extends AbstractObservable<V> {
	private final ValueFactory<K> valueFactory;
	private final K name;
	private final V defaultValue;
	private final Type type;
	private final EventRegistration eventRegistration;

	public ObservableValue(final ValueFactory<K> valueFactory,
			K name, Type type, V defaultValue) {
		this.valueFactory = valueFactory;
		this.name = name;
		this.type = type;
		this.defaultValue = defaultValue;
		this.eventRegistration = valueFactory.registerListener(name, new EventListener<ChangeEvent<K>>() {

			@Override
			public void onEvent(ChangeEvent<K> event) {
				ObservableValue.this.publishEvent(new ChangeEvent<V>(event.getEventType(), forceGet()));
			}
		});
	}
	
	@Override
	protected void finalize() throws Throwable {
		eventRegistration.unregister();
		super.finalize();
	}

	@SuppressWarnings("unchecked")
	public V forceGet() {
		return (V) valueFactory.getValue(name, type, defaultValue);
	}
}
