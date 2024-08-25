package io.basc.framework.util.register.container;

import io.basc.framework.util.register.CombinablePayloadRegistration;
import io.basc.framework.util.register.Registration;
import lombok.NonNull;

public class CombinableEntryRegistration<K, V, W extends EntryRegistration<K, V>>
		extends CombinableElementRegistration<V, W> implements EntryRegistration<K, V> {
	public CombinableEntryRegistration(W source, Registration registration) {
		super(source, registration);
	}

	protected CombinableEntryRegistration(CombinablePayloadRegistration<V, W> combinableServiceRegistration) {
		super(combinableServiceRegistration);
	}

	@Override
	public K getKey() {
		return source.getKey();
	}

	@Override
	public CombinableEntryRegistration<K, V, W> and(@NonNull Registration registration) {
		return new CombinableEntryRegistration<>(super.and(registration));
	}
}
