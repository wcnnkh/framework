package io.basc.framework.util.register;

import io.basc.framework.util.concurrent.limit.NoOpLimiter;
import lombok.NonNull;

public class StandardPayloadRegistration<W extends Registration, T> extends StandardRegistration<W>
		implements PayloadRegistration<T> {
	private final T payload;

	public StandardPayloadRegistration(@NonNull W source, T payload) {
		super(new NoOpLimiter(), source);
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
