package io.basc.framework.util.observe.register;

import java.util.concurrent.locks.Lock;
import java.util.function.BooleanSupplier;

import io.basc.framework.util.concurrent.limit.Limiter;
import io.basc.framework.util.concurrent.limit.NoOpLimiter;
import io.basc.framework.util.observe.Registration;
import lombok.NonNull;

public abstract class LimitableRegistration implements Registration {
	@NonNull
	private final Limiter limiter;

	public LimitableRegistration() {
		this(new NoOpLimiter());
	}

	public LimitableRegistration(@NonNull Limiter limiter) {
		this.limiter = limiter;
	}

	/**
	 * 限制器
	 * 
	 * @return
	 */
	public Limiter getLimiter() {
		return limiter;
	}

	@Override
	public final boolean isCancellable() {
		return isCancellable(() -> true);
	}

	/**
	 * 没有被限制且checker通过
	 * 
	 * @param checker
	 * @return
	 */
	public boolean isCancellable(BooleanSupplier checker) {
		return !limiter.isLimited() && checker.getAsBoolean();
	}

	@Override
	public final boolean cancel() {
		return cancel(() -> true);
	}

	/**
	 * 取消
	 * 
	 * @param cancel 无限限制时的取消行为
	 * @return
	 */
	public boolean cancel(BooleanSupplier cancel) {
		if (limiter.isLimited()) {
			return false;
		}

		Lock resource = limiter.getResource();
		if (resource.tryLock()) {
			try {
				return cancel.getAsBoolean();
			} finally {
				resource.unlock();
			}
		}
		return false;
	}

	@Override
	public final boolean isCancelled() {
		return isCancelled(() -> true);
	}

	/**
	 * 是否已取消
	 * 
	 * @param checker
	 * @return
	 */
	public boolean isCancelled(BooleanSupplier checker) {
		return limiter.isLimited() || checker.getAsBoolean();
	}
}
