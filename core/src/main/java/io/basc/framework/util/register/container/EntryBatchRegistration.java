package io.basc.framework.util.register.container;

import java.util.Map.Entry;
import java.util.function.Function;

import io.basc.framework.util.element.Elements;
import io.basc.framework.util.register.BatchRegistration;
import io.basc.framework.util.register.Registration;
import io.basc.framework.util.register.ServiceBatchRegistration;
import lombok.NonNull;

public class EntryBatchRegistration<K, V> extends ServiceBatchRegistration<Entry<K, V>, EntryRegistration<K, V>> {
	protected EntryBatchRegistration(BatchRegistration<EntryRegistration<K, V>> batchRegistration) {
		super(batchRegistration);
	}

	public EntryBatchRegistration(@NonNull Elements<EntryRegistration<K, V>> registrations) {
		super(registrations, (a, b) -> a.and(b));
	}

	@Override
	public EntryBatchRegistration<K, V> batch(
			@NonNull Function<? super Elements<EntryRegistration<K, V>>, ? extends Registration> batchMapper) {
		return new EntryBatchRegistration<>(super.batch(batchMapper));
	}
}
