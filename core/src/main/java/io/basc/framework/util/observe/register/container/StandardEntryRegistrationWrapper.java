package io.basc.framework.util.observe.register.container;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.register.StandardKeyValueRegistrationWrapper;
import lombok.NonNull;

public class StandardEntryRegistrationWrapper<K, V, W extends EntryRegistration<K, V>>
		extends StandardKeyValueRegistrationWrapper<K, V, W> implements EntryRegistrationWrapper<K, V, W> {

	public StandardEntryRegistrationWrapper(@NonNull W source, @NonNull Elements<Registration> relatedRegistrations) {
		super(source, relatedRegistrations);
	}

	protected StandardEntryRegistrationWrapper(StandardKeyValueRegistrationWrapper<K, V, W> context) {
		super(context);
	}

	@Override
	public StandardEntryRegistrationWrapper<K, V, W> and(@NonNull Registration registration) {
		return new StandardEntryRegistrationWrapper<>(super.and(registration));
	}
}
