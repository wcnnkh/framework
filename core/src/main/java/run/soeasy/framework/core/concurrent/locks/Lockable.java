package run.soeasy.framework.core.concurrent.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public interface Lockable extends Lock {
	@Override
	default void lock() {
		while (true) {
			try {
				lockInterruptibly();
				break;
			} catch (InterruptedException e) {
				// ignore 一直等
			}
		}
	}

	@Override
	default void lockInterruptibly() throws InterruptedException {
		while (tryLock(Long.MAX_VALUE, TimeUnit.MILLISECONDS)) {};
	}

	@Override
	default Condition newCondition() {
		throw new UnsupportedOperationException("NoOpLock can't provide a condition");
	}
}
