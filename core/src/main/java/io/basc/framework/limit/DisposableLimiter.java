package io.basc.framework.limit;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;

import io.basc.framework.locks.DisposableLock;

public class DisposableLimiter implements Limiter {
	private final AtomicBoolean limited = new AtomicBoolean();

	@Override
	public boolean isLimited() {
		return limited.get();
	}

	@Override
	public boolean limited() {
		return limited.compareAndSet(false, true);
	}

	@Override
	public Lock getResource() {
		return new DisposableResource();
	}

	private class DisposableResource extends DisposableLock {
		@Override
		public boolean tryLock() {
			return limited.compareAndSet(false, true);
		}

		@Override
		public void unlock() {
			// ignore
		}
	}
}
