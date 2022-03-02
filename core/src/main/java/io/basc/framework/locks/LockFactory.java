package io.basc.framework.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import io.basc.framework.util.stream.CallableProcessor;

@FunctionalInterface
public interface LockFactory {
	Lock getLock(String name) throws UnableToAcquireLockException;

	default <T, E extends Throwable> T process(String name, CallableProcessor<T, E> processor)
			throws E, UnableToAcquireLockException {
		Lock lock = getLock(name);
		if (lock == null) {
			throw new UnableToAcquireLockException("Lock[" + name + "] is empty");
		}

		if (lock.tryLock()) {
			try {
				return processor.process();
			} finally {
				lock.unlock();
			}
		}
		throw new UnableToAcquireLockException(name);
	}

	default <T, E extends Throwable> T process(String name, long tryLockTime, TimeUnit tryLockTimeUnit,
			CallableProcessor<T, E> processor) throws E, UnableToAcquireLockException, InterruptedException {
		Lock lock = getLock(name);
		if (lock == null) {
			throw new UnableToAcquireLockException("Lock[" + name + "] is empty");
		}

		if (lock.tryLock(tryLockTime, tryLockTimeUnit)) {
			try {
				return processor.process();
			} finally {
				lock.unlock();
			}
		}
		throw new UnableToAcquireLockException(name);
	}
}
