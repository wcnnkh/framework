package io.basc.framework.value;

import io.basc.framework.event.AbstractObservable;
import io.basc.framework.event.ChangeEvent;
import io.basc.framework.event.EventListener;
import io.basc.framework.event.EventRegistration;
import io.basc.framework.util.StaticSupplier;

import java.lang.reflect.Type;
import java.util.function.Supplier;

public class ObservableValue<K, V> extends AbstractObservable<V> {
	private final ValueFactory<K> valueFactory;
	private final K name;
	private final Supplier<? extends V> defaultValue;
	private final Type type;
	private final EventRegistration eventRegistration;
	
	@SuppressWarnings("unchecked")
	public ObservableValue(final ValueFactory<K> valueFactory,
			K name, Type type, Object defaultValue) {
		this(valueFactory, name, type, (Supplier<V>)(defaultValue == null? null:new StaticSupplier<Object>(defaultValue)));
	}

	public ObservableValue(final ValueFactory<K> valueFactory,
			K name, Type type, Supplier<? extends V> defaultValue) {
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
