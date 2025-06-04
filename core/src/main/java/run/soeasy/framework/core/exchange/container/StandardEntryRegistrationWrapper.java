package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

public class StandardEntryRegistrationWrapper<K, V, W extends EntryRegistration<K, V>>
		extends StandardKeyValueRegistrationWrapper<K, V, W> implements EntryRegistrationWrapper<K, V, W> {

	public StandardEntryRegistrationWrapper(@NonNull W source,
			@NonNull Elements<Registration> relatedRegistrations) {
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