package scw.value.factory;

import scw.event.Observable;
import scw.value.Value;

public interface ObservableValueFactory<K> extends ListenableValueFactory<K> {
	Observable<Value> getObservableValue(K key);
	
	Observable<Value> getObservableValue(K key, Value defaultValue);
}
