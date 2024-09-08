package io.basc.framework.util.observe.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.KeyValue;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.StandardRegistrationWrapper;
import lombok.NonNull;

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
