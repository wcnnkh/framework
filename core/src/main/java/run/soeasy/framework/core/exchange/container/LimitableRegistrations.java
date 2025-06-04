package run.soeasy.framework.core.exchange.container;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

import lombok.NonNull;
import lombok.ToString;
import run.soeasy.framework.core.Assert;
import run.soeasy.framework.core.collection.Elements;
import run.soeasy.framework.core.exchange.Registration;
import run.soeasy.framework.core.exchange.Registrations;

/**
 * 可组合的注册
 * 
 * @author shuchaowen
 *
 * @param <T>
 */
@ToString(callSuper = false)
public class LimitableRegistrations<T extends Registration> extends LimitableRegistration implements Registrations<T> {
	@NonNull
	private final Elements<T> elements;

	public LimitableRegistrations(@NonNull Elements<T> elements) {
		this.elements = elements;
	}

	protected LimitableRegistrations(LimitableRegistrations<T> context) {
		this.elements = context.elements;
	}

	@Override
	public Elements<T> getElements() {
		return elements;
	}

	@Override
	public boolean isCancellable(BooleanSupplier checker) {
		return super.isCancellable(Registrations.super::isCancellable);
	}

	@Override
	public boolean cancel(BooleanSupplier cancel) {
		return super.cancel(Registrations.super::cancel);
	}

	@Override
	public boolean isCancelled(BooleanSupplier checker) {
		return super.isCancelled(Registrations.super::isCancelled);
	}

	public <R extends Registration> LimitableRegistrations<R> map(@NonNull Function<? super T, ? extends R> mapper) {
		return new LimitableRegistrations<>(this.elements.map(mapper));
	}

	/**
	 * 组合
	 * 
	 * @param registration
	 * @return 返回一个新的
	 */
	public LimitableRegistrations<T> combine(@NonNull T registration) {
		Assert.requiredArgument(registration != null, "registration");
		return combineAll(Elements.singleton(registration));
	}

	/**
	 * 组合合并
	 * 
	 * @param registrations
	 * @return 返回一个新的
	 */
	public LimitableRegistrations<T> combineAll(@NonNull Elements<? extends T> registrations) {
		Assert.requiredArgument(registrations != null, "registrations");
		return new LimitableRegistrations<>(this.elements.concat(registrations));
	}
}
