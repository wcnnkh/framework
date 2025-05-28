package run.soeasy.framework.core.exchange.container.map;

import java.util.HashMap;
import java.util.Map;

import lombok.NonNull;
import run.soeasy.framework.core.exchange.container.EntryRegistration;
import run.soeasy.framework.core.function.ThrowingSupplier;

public class DefaultMapContainer<K, V> extends MapContainer<K, V, Map<K, EntryRegistration<K, V>>> {

	public DefaultMapContainer() {
		this(HashMap::new);
	}

	public DefaultMapContainer(
			@NonNull ThrowingSupplier<? extends Map<K, EntryRegistration<K, V>>, ? extends RuntimeException> containerSource) {
		super(containerSource);
	}

}
