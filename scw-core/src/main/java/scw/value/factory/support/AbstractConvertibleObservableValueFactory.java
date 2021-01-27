package scw.value.factory.support;

import java.lang.reflect.Type;

import scw.event.AbstractObservable;
import scw.event.ChangeEvent;
import scw.event.EventListener;
import scw.event.EventRegistration;
import scw.event.Observable;
import scw.value.factory.ConvertibleObservableValueFactory;

public abstract class AbstractConvertibleObservableValueFactory<K> extends AbstractObservableValueFactory<K> implements ConvertibleObservableValueFactory<K>{
	
	public final <T> Observable<T> getObservableValue(K key,
			Class<? extends T> type, T defaultValue) {
		return new ObservableValue<T>(key, type, defaultValue);
	}

	public final Observable<Object> getObservableValue(K key, Type type,
			Object defaultValue) {
		return new ObservableValue<Object>(key, type, defaultValue);
	}
	
	private final class ObservableValue<T> extends AbstractObservable<T> {
		private final K name;
		private final T defaultValue;
		private final Type type;

		public ObservableValue(K name, Type type, T defaultValue) {
			this.name = name;
			this.type = type;
			this.defaultValue = defaultValue;
			setRegisterOnlyExists(false);
			register();
		}

		@SuppressWarnings("unchecked")
		public T forceGet() {
			return (T) getValue(name, type, defaultValue);
		}

		public EventRegistration registerListener(boolean exists,
				final EventListener<ChangeEvent<T>> eventListener) {
			if (exists && !containsKey(name)) {
				return EventRegistration.EMPTY;
			}

			return AbstractConvertibleObservableValueFactory.this.registerListener(name,
					new EventListener<ChangeEvent<K>>() {
						public void onEvent(ChangeEvent<K> event) {
							eventListener.onEvent(new ChangeEvent<T>(event, forceGet()));
						}
					});
		}
	}
}
