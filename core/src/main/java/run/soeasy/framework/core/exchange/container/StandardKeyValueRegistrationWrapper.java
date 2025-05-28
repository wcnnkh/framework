package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Registration;

public class StandardKeyValueRegistrationWrapper<K, V, W extends KeyValueRegistration<K, V>>
		extends StandardPayloadRegistrationWrapper<KeyValue<K, V>, W> implements KeyValueRegistrationWrapper<K, V, W> {

	public StandardKeyValueRegistrationWrapper(@NonNull W source,
			@NonNull Elements<Registration> relatedRegistrations) {
		super(source, relatedRegistrations);
	}

	protected StandardKeyValueRegistrationWrapper(StandardRegistrationWrapper<W> context) {
		super(context);
	}

	@Override
	public StandardKeyValueRegistrationWrapper<K, V, W> and(Registration registration) {
		return new StandardKeyValueRegistrationWrapper<>(super.combine(registration));
	}

	@Override
	public KeyValue<K, V> getPayload() {
		return super.getPayload();
	}
}