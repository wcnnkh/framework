package io.basc.framework.util.register;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

import io.basc.framework.util.collections.Elements;
import io.basc.framework.util.exchange.Registration;
import lombok.NonNull;

/**
 * 可以拦截的
 * 
 * @author shuchaowen
 *
 * @param <B>
 * @param <T>
 * @param <A>
 */
public class InterceptableRegisration<B extends Registration, T extends Registration, A extends Registration>
		extends LimitableRegistrations<T> {
	/**
	 * 后置处理
	 */
	private final LimitableRegistrations<A> postRegistration;
	/**
	 * 前置处理
	 */
	private final LimitableRegistrations<B> preRegistration;

	private InterceptableRegisration(LimitableRegistrations<T> combinableRegistration,
			LimitableRegistrations<B> preRegistration, LimitableRegistrations<A> postRegistration) {
		super(combinableRegistration);
		this.preRegistration = preRegistration;
		this.postRegistration = postRegistration;
	}

	protected InterceptableRegisration(@NonNull InterceptableRegisration<B, T, A> interceptableRegisration) {
		this(interceptableRegisration, interceptableRegisration.preRegistration,
				interceptableRegisration.postRegistration);
	}

	public InterceptableRegisration(@NonNull Elements<T> registrations) {
		super(registrations);
		this.preRegistration = new LimitableRegistrations<>(Elements.empty());
		this.postRegistration = new LimitableRegistrations<>(Elements.empty());
	}

	@Override
	public InterceptableRegisration<B, T, A> combine(@NonNull T registration) {
		return new InterceptableRegisration<>(super.combine(registration), this.preRegistration, this.postRegistration);
	}

	@Override
	public InterceptableRegisration<B, T, A> combineAll(@NonNull Elements<? extends T> registrations) {
		return new InterceptableRegisration<>(super.combineAll(registrations), this.preRegistration,
				this.postRegistration);
	}

	@Override
	public boolean cancel(BooleanSupplier cancel) {
		return super.cancel(() -> {
			if (preRegistration.isCancellable()) {
				preRegistration.cancel();
			}

			try {
				return cancel.getAsBoolean();
			} finally {
				if (postRegistration.isCancellable()) {
					postRegistration.cancel();
				}
			}
		});
	}

	@Override
	public boolean isCancellable(BooleanSupplier checker) {
		return super.isCancellable(
				() -> preRegistration.isCancellable() || checker.getAsBoolean() || postRegistration.isCancellable());
	}

	@Override
	public boolean isCancelled(BooleanSupplier checker) {
		return super.isCancelled(
				() -> preRegistration.isCancelled() && checker.getAsBoolean() && postRegistration.isCancelled());
	}

	@Override
	public <R extends Registration> InterceptableRegisration<B, R, A> map(
			@NonNull Function<? super T, ? extends R> mapper) {
		return new InterceptableRegisration<>(super.map(mapper), this.preRegistration, this.postRegistration);
	}

	/**
	 * 后置处理
	 * 
	 * @param registration
	 * @return
	 */
	public InterceptableRegisration<B, T, A> post(@NonNull A registration) {
		return new InterceptableRegisration<>(this, this.preRegistration, this.postRegistration.combine(registration));
	}

	/**
	 * 前置处理
	 * 
	 * @param registration
	 * @return
	 */
	public InterceptableRegisration<B, T, A> pre(@NonNull B registration) {
		return new InterceptableRegisration<>(this, this.preRegistration.combine(registration), this.postRegistration);
	}
}
