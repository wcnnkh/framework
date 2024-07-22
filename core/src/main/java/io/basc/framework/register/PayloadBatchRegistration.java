package io.basc.framework.register;

import java.util.function.Function;

import io.basc.framework.util.element.Elements;
import lombok.NonNull;

public class PayloadBatchRegistration<E> extends BatchRegistration<PayloadRegistration<E>> {

	public PayloadBatchRegistration(@NonNull Iterable<? extends E> elements) {
		this(Elements.of(elements).map(PayloadRegistration::new));
	}

	public PayloadBatchRegistration(Elements<PayloadRegistration<E>> registrations) {
		super(registrations, (a, b) -> a.and(b));
	}

	private PayloadBatchRegistration(BatchRegistration<PayloadRegistration<E>> batchRegistration) {
		super(batchRegistration);
	}

	public Elements<E> getElements() {
		return getServices().map((e) -> e.getPayload());
	}

	@Override
	public PayloadBatchRegistration<E> batch(
			Function<? super Elements<PayloadRegistration<E>>, ? extends Registration> batchMapper) {
		BatchRegistration<PayloadRegistration<E>> batchRegistration = super.batch(batchMapper);
		return new PayloadBatchRegistration<>(batchRegistration);
	}
}
