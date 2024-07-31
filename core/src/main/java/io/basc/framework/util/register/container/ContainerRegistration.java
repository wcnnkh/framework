package io.basc.framework.util.register.container;

import io.basc.framework.util.concurrent.limit.DisposableLimiter;
import io.basc.framework.util.register.AbstractPayloadRegistration;
import io.basc.framework.util.register.CombinableRegistration;
import io.basc.framework.util.register.Registration;

public abstract class ContainerRegistration<T> extends AbstractPayloadRegistration<T, Registration> {

	public ContainerRegistration() {
		super(new DisposableLimiter());
	}

	protected ContainerRegistration(CombinableRegistration<Registration> context) {
		super(context);
	}
}
