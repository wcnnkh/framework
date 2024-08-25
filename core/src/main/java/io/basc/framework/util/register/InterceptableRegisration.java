package io.basc.framework.util.register;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

import io.basc.framework.util.concurrent.limit.Limiter;
import io.basc.framework.util.element.Elements;
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
		extends CombinableRegistration<T> {
	/**
	 * 后置处理
	 */
	private final CombinableRegistration<A> postRegistration;
	/**
	 * 前置处理
	 */
	private final CombinableRegistration<B> preRegistration;

	private InterceptableRegisration(CombinableRegistration<T> combinableRegistration,
			CombinableRegistration<B> preRegistration, CombinableRegistration<A> postRegistration) {
		super(combinableRegistration);
		this.preRegistration = preRegistration;
		this.postRegistration = postRegistration;
	}

	protected InterceptableRegisration(@NonNull InterceptableRegisration<B, T, A> interceptableRegisration) {
		this(interceptableRegisration, interceptableRegisration.preRegistration,
				interceptableRegisration.postRegistration);
	}

	public InterceptableRegisration(@NonNull Limiter limiter, @NonNull Elements<T> registrations) {
		super(limiter, registrations);
		this.preRegistration = new CombinableRegistration<>(Elements.empty());
		this.postRegistration = new CombinableRegistration<>(Elements.empty());
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
	public void deregister(Runnable runnable) throws RegistrationException {
		super.deregister(() -> {
			preRegistration.deregister();
			try {
				runnable.run();
			} finally {
				postRegistration.deregister();
			}
		});
	}

	@Override
	public boolean isInvalid(BooleanSupplier checker) {
		return super.isInvalid(
				() -> preRegistration.isInvalid() && checker.getAsBoolean() && postRegistration.isInvalid());
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
