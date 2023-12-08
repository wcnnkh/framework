package io.basc.framework.observe.properties;

import io.basc.framework.event.batch.BatchEventRegistry;
import io.basc.framework.observe.Observable;
import io.basc.framework.value.ValueFactory;

public interface ObservableValueFactory<K>
		extends ValueFactory<K>, BatchEventRegistry<PropertyChangeEvent<K>>, Observable<PropertyChangeEvent<K>> {
}
