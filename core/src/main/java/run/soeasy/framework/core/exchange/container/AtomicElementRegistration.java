package run.soeasy.framework.core.exchange.container;

import java.util.concurrent.atomic.AtomicReference;

import lombok.Getter;
import run.soeasy.framework.core.exchange.Lifecycle;

@Getter
public class AtomicElementRegistration<E> extends AbstractPayloadRegistration<E> implements ElementRegistration<E> {
	private final AtomicReference<E> payloadReference;
	private Lifecycle lifecycle;

	public AtomicElementRegistration(E initialValue) {
		this.payloadReference = new AtomicReference<>(initialValue);
	}

	@Override
	public E getPayload() {
		return payloadReference.get();
	}

	@Override
	public E setPayload(E payload) {
		if (getLimiter().isLimited()) {
			throw new UnsupportedOperationException("Limited");
		}
		return payloadReference.getAndSet(payload);
	}

	public void setLifecycle(Lifecycle lifecycle) {
		this.lifecycle = lifecycle;
	}
}
