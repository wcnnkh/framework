package run.soeasy.framework.core.exchange.container;

import lombok.NonNull;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

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