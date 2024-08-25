package io.basc.framework.util.register;

import java.util.function.Function;

import io.basc.framework.util.Assert;
import io.basc.framework.util.concurrent.limit.Limiter;
import io.basc.framework.util.concurrent.limit.NoOpLimiter;
import io.basc.framework.util.element.Elements;
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
public class CombinableRegistration<T extends Registration> extends AbstractRegistration implements Registrations<T> {
	@NonNull
	private final Elements<T> registrations;

	public CombinableRegistration(@NonNull Elements<T> registrations) {
		this(new NoOpLimiter(), registrations);
	}

	public CombinableRegistration(@NonNull Limiter limiter, @NonNull Elements<T> registrations) {
		super(limiter);
		this.registrations = registrations;
	}

	protected CombinableRegistration(CombinableRegistration<T> combinableRegistration) {
		this(combinableRegistration, combinableRegistration.registrations);
	}

	private CombinableRegistration(@NonNull AbstractRegistration abstractRegistration,
			@NonNull Elements<T> registrations) {
		super(abstractRegistration);
		this.registrations = registrations;
	}

	@Override
	public Elements<T> getRegistrations() {
		return registrations;
	}

	@Override
	public final boolean isInvalid() {
		return isInvalid(Registrations.super::isInvalid);
	}

	@Override
	public final void deregister() throws RegistrationException {
		deregister(Registrations.super::deregister);
	}

	public <R extends Registration> CombinableRegistration<R> map(@NonNull Function<? super T, ? extends R> mapper) {
		return new CombinableRegistration<>(this, this.registrations.map(mapper));
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
		return new CombinableRegistration<>(this, this.registrations.concat(registrations));
	}
}
