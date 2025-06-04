package run.soeasy.framework.core.exchange.container;

import run.soeasy.framework.core.exchange.Registed;
import run.soeasy.framework.core.exchange.Registration;


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