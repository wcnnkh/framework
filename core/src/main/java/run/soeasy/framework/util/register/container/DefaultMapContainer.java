package run.soeasy.framework.util.register.container;

import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.util.function.Supplier;

public class DefaultMapContainer<K, V> extends MapContainer<K, V, Map<K, EntryRegistration<K, V>>> {

	public DefaultMapContainer() {
		this(HashMap::new);
	}

	public DefaultMapContainer(
			@NonNull Supplier<? extends Map<K, EntryRegistration<K, V>>, ? extends RuntimeException> containerSource) {
		super(containerSource);
	}

}
