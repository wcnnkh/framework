package io.basc.framework.util.register;

import java.util.concurrent.locks.Lock;
import java.util.function.BooleanSupplier;

import io.basc.framework.util.concurrent.limit.Limiter;
import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;
import lombok.NonNull;

public abstract class LimitableRegistration implements Registration {
	private static Logger logger = LoggerFactory.getLogger(LimitableRegistration.class);
	@NonNull
	private final Limiter limiter;

	public LimitableRegistration(@NonNull Limiter limiter) {
		this.limiter = limiter;
	}

	public LimitableRegistration(@NonNull LimitableRegistration limitableRegistration) {
		this.limiter = limitableRegistration.limiter;
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
	public void deregister(Runnable runnable) throws RegistrationException {
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
	public final void deregister() throws RegistrationException {
		deregister(() -> {
			logger.trace("Execute {}#deregister", LimitableRegistration.this.getClass());
		});
	}

	@Override
	public final boolean isInvalid() {
		return isInvalid(Registration.super::isInvalid);
	}
}
