package io.basc.framework.util.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import io.basc.framework.util.logging.Logger;
import io.basc.framework.util.logging.LoggerFactory;

public interface Lockable extends Lock {
	static Logger defaultLogger = LoggerFactory.getLogger(Lockable.class);

	@Override
	default void lock() {
		while (true) {
			try {
				lockInterruptibly();
				break;
			} catch (InterruptedException e) {
				// ignore 一直等
				defaultLogger.trace(e, "ignore this error");
			}
		}
	}

	@Override
	default void lockInterruptibly() throws InterruptedException {
		while (tryLock(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {
			defaultLogger.warn("Exceeded maximum duration");
		}
	}

	@Override
	default Condition newCondition() {
		throw new UnsupportedOperationException("NoOpLock can't provide a condition");
	}
}
