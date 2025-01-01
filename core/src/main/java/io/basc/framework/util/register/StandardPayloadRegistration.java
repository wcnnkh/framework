package io.basc.framework.util.register;

import io.basc.framework.util.Elements;
import io.basc.framework.util.exchange.Registration;
import lombok.NonNull;

public class StandardPayloadRegistration<W extends Registration, T> extends StandardRegistrationWrapper<W>
		implements PayloadRegistration<T> {
	private final T payload;

	public StandardPayloadRegistration(@NonNull W source, T payload) {
		super(source, Elements.empty());
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
