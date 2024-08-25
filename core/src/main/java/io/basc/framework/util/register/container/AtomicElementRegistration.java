package io.basc.framework.util.register.container;

import java.util.concurrent.atomic.AtomicReference;

import io.basc.framework.util.register.CombinableRegistration;
import io.basc.framework.util.register.Registration;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class AtomicElementRegistration<E> extends AbstractPayloadRegistration<E> implements ElementRegistration<E> {
	private final AtomicReference<E> reference;

	public AtomicElementRegistration(E initialValue) {
		this.reference = new AtomicReference<>(initialValue);
	}

	protected AtomicElementRegistration(AtomicElementRegistration<E> elementRegistration) {
		this(elementRegistration, elementRegistration.reference);
	}

	private AtomicElementRegistration(CombinableRegistration<Registration> context, AtomicReference<E> reference) {
		super(context);
		this.reference = reference;
	}

	@Override
	public AtomicElementRegistration<E> combine(@NonNull Registration registration) {
		return new AtomicElementRegistration<>(super.combine(registration), this.reference);
	}

	@Override
	public E getValue() {
		return reference.get();
	}

	@Override
	public E setValue(E value) {
		if (isInvalid()) {
			throw new UnsupportedOperationException("Invalid");
		}
		return reference.getAndSet(value);
	}
}
