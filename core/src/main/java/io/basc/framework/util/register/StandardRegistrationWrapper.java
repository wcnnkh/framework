package io.basc.framework.util.register;

import java.util.function.BooleanSupplier;

import io.basc.framework.util.Elements;
import io.basc.framework.util.Registration;
import io.basc.framework.util.Wrapper;
import lombok.NonNull;

public class StandardRegistrationWrapper<W extends Registration>
		extends InterceptableRegisration<Registration, Registration, Registration> implements Wrapper<W> {
	@NonNull
	private final W source;

	/**
	 * 包装一个Registration
	 * 
	 * @param source
	 * @param relatedRegistrations 与之相关的
	 */
	public StandardRegistrationWrapper(@NonNull W source, @NonNull Elements<Registration> relatedRegistrations) {
		super(relatedRegistrations);
		this.source = source;
	}

	protected StandardRegistrationWrapper(StandardRegistrationWrapper<W> wrapper) {
		this(wrapper, wrapper.source);
	}

	private StandardRegistrationWrapper(InterceptableRegisration<Registration, Registration, Registration> context,
			W source) {
		super(context);
		this.source = source;
	}

	@Override
	public W getSource() {
		return source;
	}

	@Override
	public Registration and(Registration registration) {
		return combine(registration);
	}

	@Override
	public StandardRegistrationWrapper<W> combine(@NonNull Registration registration) {
		return new StandardRegistrationWrapper<>(super.combine(registration), source);
	}

	@Override
	public StandardRegistrationWrapper<W> combineAll(@NonNull Elements<? extends Registration> registrations) {
		return new StandardRegistrationWrapper<>(super.combineAll(registrations), source);
	}

	@Override
	public boolean isCancellable(BooleanSupplier checker) {
		return super.isCancellable(() -> checker.getAsBoolean() || source.isCancellable());
	}

	@Override
	public boolean isCancelled(BooleanSupplier checker) {
		return super.isCancelled(() -> checker.getAsBoolean() && source.isCancelled());
	}

	@Override
	public boolean cancel(BooleanSupplier cancel) {
		return super.cancel(() -> {
			try {
				return source.cancel();
			} finally {
				cancel.getAsBoolean();
			}
		});
	}
}
