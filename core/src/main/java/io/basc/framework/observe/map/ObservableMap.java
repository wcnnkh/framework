package io.basc.framework.observe.map;

import java.util.Map;
import java.util.Map.Entry;

import io.basc.framework.observe.Observable;
import io.basc.framework.observe.Observer;

public interface ObservableMap<K, V> extends Observable, Observer<Entry<K, V>>, Map<K, V> {
}
