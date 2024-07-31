package io.basc.framework.util.register.container;

import java.util.Collection;
import java.util.function.Supplier;

public class MultiValueRegistry<K, V, C extends Collection<ElementRegistration<V>>> extends ElementRegistry<V, C> {
	private final K key;

	public MultiValueRegistry(K key, Supplier<? extends C> containerSupplier) {
		super(containerSupplier);
		this.key = key;
	}

	public K getKey() {
		return key;
	}
}
