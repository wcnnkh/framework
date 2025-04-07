package run.soeasy.framework.util.register;

import lombok.NonNull;
import run.soeasy.framework.util.collection.Elements;
import run.soeasy.framework.util.exchange.Registration;

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
