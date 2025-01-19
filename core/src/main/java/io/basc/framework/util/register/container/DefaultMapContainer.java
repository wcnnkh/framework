package io.basc.framework.util.register.container;

import java.util.HashMap;
import java.util.Map;

import io.basc.framework.util.function.Supplier;
import lombok.NonNull;

public class DefaultMapContainer<K, V> extends MapContainer<K, V, Map<K, EntryRegistration<K, V>>> {

	public DefaultMapContainer() {
		this(HashMap::new);
	}

	public DefaultMapContainer(
			@NonNull Supplier<? extends Map<K, EntryRegistration<K, V>>, ? extends RuntimeException> containerSource) {
		super(containerSource);
	}

}
