package io.basc.framework.util.register;

import io.basc.framework.util.Registed;
import io.basc.framework.util.Registration;

public class PayloadRegisted<T> extends Registed implements PayloadRegistration<T> {
	private static final long serialVersionUID = 1L;
	private final T payload;

	public PayloadRegisted(boolean cancelled, T payload) {
		super(cancelled);
		this.payload = payload;
	}

	@Override
	public PayloadRegistration<T> and(Registration registration) {
		return PayloadRegistration.super.and(registration);
	}

	@Override
	public T getPayload() {
		return payload;
	}
}
