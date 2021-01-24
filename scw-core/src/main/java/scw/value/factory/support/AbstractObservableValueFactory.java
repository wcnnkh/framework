package scw.value.factory.support;

import scw.event.AbstractObservable;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.Observable;
import scw.value.EmptyValue;
import scw.value.Value;
import scw.value.factory.ObservableValueFactory;

public abstract class AbstractObservableValueFactory<K> extends AbstractConvertibleValueFactory<K> implements ObservableValueFactory<K>{
	
	public Observable<Value> getObservableValue(K key) {
		return getObservableValue(key, EmptyValue.INSTANCE);
	}
	
	public Observable<Value> getObservableValue(K key, Value defaultValue) {
		return new ObservableValue(key, defaultValue);
	}
	
	private final class ObservableValue extends AbstractObservable<Value> {
		private final K name;
		private final Value defaultValue;

		public ObservableValue(K name, Value defaultValue) {
			this.name = name;
			this.defaultValue = defaultValue;
			setRegisterOnlyExists(false);
			register();
		}

		public Value forceGet() {
			Value value = getValue(name);
			return value == null? defaultValue:value;
		}

		public EventRegistration registerListener(boolean exists,
				final EventListener<ChangeEvent<Value>> eventListener) {
			if (exists && !containsKey(name)) {
				return EventRegistration.EMPTY;
			}

			return AbstractObservableValueFactory.this.registerListener(name,
					new EventListener<ChangeEvent<K>>() {
						public void onEvent(ChangeEvent<K> event) {
							eventListener.onEvent(new ChangeEvent<Value>(event, forceGet()));
						}
					});
		}
	}
}
