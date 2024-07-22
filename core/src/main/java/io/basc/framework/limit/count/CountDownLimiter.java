package io.basc.framework.limit.count;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import io.basc.framework.limit.Limiter;
import io.basc.framework.locks.DisposableLock;
import io.basc.framework.locks.NoOpLock;

/**
 * 通过只能减小的次数来限制
 * 
 * @author shuchaowen
 *
 */
public class CountDownLimiter implements Limiter {
	private final AtomicLong count;

	public CountDownLimiter(long count) {
		this.count = new AtomicLong(count);
	}

	@Override
	public boolean isLimited() {
		return count.get() <= 0;
	}

	@Override
	public boolean limited() {
		return count.getAndSet(0) > 0;
	}

	@Override
	public Lock getResource() {
		if (isLimited()) {
			return NoOpLock.DEAD;
		}
		return new Resource();
	}

	private class Resource extends DisposableLock {

		@Override
		public boolean tryLock() {
			return count.decrementAndGet() >= 0;
		}

		@Override
		public void unlock() {
			// ignore
		}
	}
}
