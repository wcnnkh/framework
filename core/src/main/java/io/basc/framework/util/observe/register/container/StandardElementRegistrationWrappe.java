package io.basc.framework.util.observe.register.container;

import io.basc.framework.util.Elements;
import io.basc.framework.util.observe.Registration;
import io.basc.framework.util.observe.register.StandardPayloadRegistrationWrapper;
import lombok.NonNull;

public class StandardElementRegistrationWrappe<V, W extends ElementRegistration<V>>
		extends StandardPayloadRegistrationWrapper<V, W> implements ElementRegistrationWrapper<V, W> {

	public StandardElementRegistrationWrappe(@NonNull W source, @NonNull Elements<Registration> relatedRegistrations) {
		super(source, relatedRegistrations);
	}

	protected StandardElementRegistrationWrappe(StandardPayloadRegistrationWrapper<V, W> context) {
		super(context);
	}

	@Override
	public StandardElementRegistrationWrappe<V, W> and(@NonNull Registration registration) {
		return new StandardElementRegistrationWrappe<>(super.and(registration));
	}
}
