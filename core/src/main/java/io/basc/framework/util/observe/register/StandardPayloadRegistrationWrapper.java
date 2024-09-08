package io.basc.framework.util.observe.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.StandardRegistrationWrapper;
import lombok.NonNull;

public class StandardPayloadRegistrationWrapper<S, W extends PayloadRegistration<S>>
		extends StandardRegistrationWrapper<W> implements PayloadRegistrationWrapper<S, W> {

	public StandardPayloadRegistrationWrapper(@NonNull W source, @NonNull Elements<Registration> relatedRegistrations) {
		super(source, relatedRegistrations);
	}

	protected StandardPayloadRegistrationWrapper(StandardRegistrationWrapper<W> context) {
		super(context);
	}

	@Override
	public StandardPayloadRegistrationWrapper<S, W> and(@NonNull Registration registration) {
		return combine(registration);
	}

	@Override
	public StandardPayloadRegistrationWrapper<S, W> combine(@NonNull Registration registration) {
		return new StandardPayloadRegistrationWrapper<>(super.combine(registration));
	}
}
