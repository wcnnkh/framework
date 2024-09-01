package io.basc.framework.util.register;

import java.util.function.BooleanSupplier;

import io.basc.framework.util.concurrent.limit.Limiter;
import lombok.NonNull;

public class StandardRegistration<W extends Registration> extends LimitableRegistration
		implements RegistrationWrapper<W> {
	private final W source;

	public StandardRegistration(@NonNull Limiter limiter, @NonNull W source) {
		super(limiter);
		this.source = source;
	}

	protected StandardRegistration(@NonNull StandardRegistration<W> standardRegistration) {
		this(standardRegistration, standardRegistration.source);
	}

	private StandardRegistration(LimitableRegistration limitableRegistration, W source) {
		super(limitableRegistration);
		this.source = source;
	}

	@Override
	public boolean isInvalid(BooleanSupplier checker) {
		return super.isInvalid(source::isInvalid);
	}

	@Override
	public void deregister(Runnable runnable) throws RegistrationException {
		deregister(source::deregister);
	}

	@Override
	public W getSource() {
		return source;
	}
}
