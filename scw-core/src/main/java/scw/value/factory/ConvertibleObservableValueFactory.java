package scw.value.factory;

import java.lang.reflect.Type;

import scw.event.Observable;

public interface ConvertibleObservableValueFactory<K> extends
		ObservableValueFactory<K>, ConvertibleValueFactory<K> {
	<T> Observable<T> getObservableValue(K key, Class<? extends T> type,
			T defaultValue);

	Observable<Object> getObservableValue(K key, Type type, Object defaultValue);
}
