package io.basc.framework.observe.properties;

import java.util.Map;

import io.basc.framework.observe.Observable;
import io.basc.framework.observe.PayloadChangeEvent;

public interface ObservableMap<K, V> extends Map<K, V>, Observable<PayloadChangeEvent<Map<? extends K, ? extends V>>> {

}
