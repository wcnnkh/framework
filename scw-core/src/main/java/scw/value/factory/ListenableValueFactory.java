package scw.value.factory;

import scw.event.ChangeEvent;
import scw.event.NamedEventRegistry;

public interface ListenableValueFactory<K> extends ValueFactory<K>,
		NamedEventRegistry<K, ChangeEvent<K>> {
}
