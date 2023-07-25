package io.basc.framework.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

import io.basc.framework.util.function.Source;

@FunctionalInterface
public interface LockFactory {
	Lock getLock(String name) throws UnableToAcquireLockException;

	default <T, E extends Throwable> T process(String name, Source<? extends T, ? extends E> source)
			throws E, UnableToAcquireLockException {
		Lock lock = getLock(name);
		if (lock == null) {
			throw new UnableToAcquireLockException("Lock[" + name + "] is empty");
		}

		if (lock.tryLock()) {
			try {
				return source.get();
			} finally {
				lock.unlock();
			}
		}
		throw new UnableToAcquireLockException(name);
	}

	default <T, E extends Throwable> T process(String name, long tryLockTime, TimeUnit tryLockTimeUnit,
			Source<? extends T, ? extends E> source) throws E, UnableToAcquireLockException, InterruptedException {
		Lock lock = getLock(name);
		if (lock == null) {
			throw new UnableToAcquireLockException("Lock[" + name + "] is empty");
		}

		if (lock.tryLock(tryLockTime, tryLockTimeUnit)) {
			try {
				return source.get();
			} finally {
				lock.unlock();
			}
		}
		throw new UnableToAcquireLockException(name);
	}
}
