package io.basc.framework.register;

import java.util.concurrent.locks.Lock;
import java.util.function.BooleanSupplier;

import io.basc.framework.limit.Limiter;
import lombok.NonNull;

public abstract class AbstractRegistration implements Registration {
	@NonNull
	private final Limiter limiter;

	public AbstractRegistration(@NonNull Limiter limiter) {
		this.limiter = limiter;
	}

	public AbstractRegistration(@NonNull AbstractRegistration abstractRegistration) {
		this.limiter = abstractRegistration.limiter;
	}

	/**
	 * 限制器
	 * 
	 * @return
	 */
	public Limiter getLimiter() {
		return limiter;
	}

	/**
	 * 静态代理isInvalid行为，方便做前后置处理
	 * 
	 * @param checker
	 * @return
	 */
	public boolean isInvalid(BooleanSupplier checker) {
		return limiter.isLimited() || checker.getAsBoolean();
	}

	/**
	 * 静态代理unregister行为，方便做前后置处理
	 * 
	 * @param runnable
	 * @throws RegistrationException
	 */
	public void unregister(Runnable runnable) throws RegistrationException {
		Lock resource = limiter.getResource();
		if (resource.tryLock()) {
			try {
				runnable.run();
			} finally {
				resource.unlock();
			}
		}
	}

	@Override
	public abstract boolean isInvalid();
}
