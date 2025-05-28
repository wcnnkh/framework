package run.soeasy.framework.core.exchange.container;

import java.util.Map.Entry;

import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

public interface EntryRegistration<K, V> extends KeyValueRegistration<K, V>, Entry<K, V> {

	@Override
	default EntryRegistration<K, V> and(Registration registration) {
		if (registration == null || registration.isCancelled()) {
			return this;
		}

		return new StandardEntryRegistrationWrapper<>(this, Elements.singleton(registration));
	}
}
