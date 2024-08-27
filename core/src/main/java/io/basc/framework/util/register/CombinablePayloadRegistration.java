package io.basc.framework.util.register;

import java.util.function.BooleanSupplier;

import io.basc.framework.util.Elements;
import lombok.NonNull;

public class CombinablePayloadRegistration<S, W extends PayloadRegistration<S>>
		extends CombinableRegistration<Registration> implements PayloadRegistration<S> {
	protected final W source;

	public CombinablePayloadRegistration(W source, Registration registration) {
		super(Elements.singleton(registration));
		this.source = source;
	}

	protected CombinablePayloadRegistration(CombinablePayloadRegistration<S, W> combinableServiceRegistration) {
		this(combinableServiceRegistration.source, combinableServiceRegistration);
	}

	private CombinablePayloadRegistration(W source, CombinableRegistration<Registration> context) {
		super(context);
		this.source = source;
	}

	@Override
	public CombinablePayloadRegistration<S, W> and(@NonNull Registration registration) {
		return combine(registration);
	}

	@Override
	public CombinablePayloadRegistration<S, W> combine(@NonNull Registration registration) {
		return new CombinablePayloadRegistration<>(source, super.combine(registration));
	}

	@Override
	public CombinablePayloadRegistration<S, W> combineAll(@NonNull Elements<? extends Registration> registrations) {
		return new CombinablePayloadRegistration<>(source, super.combineAll(registrations));
	}

	@Override
	public boolean isInvalid(BooleanSupplier checker) {
		return super.isInvalid(() -> source.isInvalid() && checker.getAsBoolean());
	}

	@Override
	public void deregister(Runnable runnable) throws RegistrationException {
		super.deregister(() -> {
			try {
				source.deregister();
			} finally {
				runnable.run();
			}
		});
	}

	@Override
	public S getPayload() {
		return source.getPayload();
	}
}
