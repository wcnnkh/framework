package io.basc.framework.util.observe.register;

import io.basc.framework.util.observe.Cancelled;
import io.basc.framework.util.observe.Registration;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PayloadCancelled<T> extends Cancelled implements PayloadRegistration<T> {
	private final T payload;

	@Override
	public PayloadRegistration<T> and(Registration registration) {
		return PayloadRegistration.super.and(registration);
	}

	@Override
	public T getPayload() {
		return payload;
	}
}
