package scw.value;

import java.lang.reflect.Type;

import scw.event.ChangeEvent;
import scw.event.NamedEventRegistry;
import scw.event.Observable;
import scw.lang.Nullable;

public interface ListenableValueFactory<K> extends ValueFactory<K>,
		NamedEventRegistry<K, ChangeEvent<K>> {
	default Observable<Value> getObservableValue(K key) {
		return new ObservableValue<K, Value>(this, key, Value.class, null);
	}

	default Observable<Value> getObservableValue(K key,
			@Nullable Value defaultValue) {
		return new ObservableValue<K, Value>(this, key, Value.class,
				defaultValue);
	}

	default <T> Observable<T> getObservableValue(K key, Class<? extends T> type) {
		return new ObservableValue<K, T>(this, key, type, null);
	}

	default <T> Observable<T> getObservableValue(K key,
			Class<? extends T> type, T defaultValue) {
		return new ObservableValue<K, T>(this, key, type, defaultValue);
	}

	default Observable<Object> getObservableValue(K key, Type type) {
		return new ObservableValue<K, Object>(this, key, type, null);
	}

	default Observable<Object> getObservableValue(K key, Type type,
			Object defaultValue) {
		return new ObservableValue<K, Object>(this, key, type, defaultValue);
	}
}
