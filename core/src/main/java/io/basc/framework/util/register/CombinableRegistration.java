package io.basc.framework.util.register;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

import io.basc.framework.util.Assert;
import io.basc.framework.util.Elements;
import io.basc.framework.util.concurrent.limit.Limiter;
import io.basc.framework.util.concurrent.limit.NoOpLimiter;
import lombok.NonNull;
import lombok.ToString;

/**
 * 可组合的注册
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
@ToString(callSuper = false)
public class CombinableRegistration<T extends Registration> extends LimitableRegistration implements Registrations<T> {
	@NonNull
	private final Elements<T> elements;

	public CombinableRegistration(@NonNull Elements<T> elements) {
		this(new NoOpLimiter(), elements);
	}

	public CombinableRegistration(@NonNull Limiter limiter, @NonNull Elements<T> elements) {
		super(limiter);
		this.elements = elements;
	}

	protected CombinableRegistration(CombinableRegistration<T> combinableRegistration) {
		this(combinableRegistration, combinableRegistration.elements);
	}

	private CombinableRegistration(@NonNull LimitableRegistration abstractRegistration, @NonNull Elements<T> elements) {
		super(abstractRegistration);
		this.elements = elements;
	}

	@Override
	public Elements<T> getElements() {
		return elements;
	}

	@Override
	public boolean isInvalid(BooleanSupplier checker) {
		return super.isInvalid(Registrations.super::isInvalid);
	}

	@Override
	public void deregister(Runnable runnable) throws RegistrationException {
		super.deregister(Registrations.super::deregister);
	}

	public <R extends Registration> CombinableRegistration<R> map(@NonNull Function<? super T, ? extends R> mapper) {
		return new CombinableRegistration<>(this, this.elements.map(mapper));
	}

	/**
	 * 组合
	 * 
	 * @param registration
	 * @return 返回一个新的
	 */
	public CombinableRegistration<T> combine(@NonNull T registration) {
		Assert.requiredArgument(registration != null, "registration");
		return combineAll(Elements.singleton(registration));
	}

	/**
	 * 组合合并
	 * 
	 * @param registrations
	 * @return 返回一个新的
	 */
	public CombinableRegistration<T> combineAll(@NonNull Elements<? extends T> registrations) {
		Assert.requiredArgument(registrations != null, "registrations");
		return new CombinableRegistration<>(this, this.elements.concat(registrations));
	}
}
