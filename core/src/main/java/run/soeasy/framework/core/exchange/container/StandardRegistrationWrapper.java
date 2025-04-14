package run.soeasy.framework.core.exchange.container;

import java.util.function.BooleanSupplier;

import lombok.NonNull;
import run.soeasy.framework.core.Wrapper;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;

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
		return super.cancel(() -> cancel.getAsBoolean() && source.cancel());
	}
}
