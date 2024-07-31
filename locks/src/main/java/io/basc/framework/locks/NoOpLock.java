package io.basc.framework.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import io.basc.framework.util.concurrent.locks.NoOpLock;

public final class NoOpLock implements Lock {
	/**
	 * 无锁的
	 */
	public static final NoOpLock NO = new NoOpLock(false);
	/**
	 * 死锁
	 */
	public static final NoOpLock DEAD = new NoOpLock(true);

	private final boolean lock;

	public NoOpLock(boolean lock) {
		this.lock = lock;
	}

	public void lock() {
		if (lock) {
			while (true) {
				try {
					lockInterruptibly();
				} catch (InterruptedException e) {
				}
			}
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		if (lock) {
			while (true) {
				Thread.sleep(Long.MAX_VALUE);
			}
		}
	}

	public boolean tryLock() {
		return !lock;
	}

	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return !lock;
	}

	public void unlock() {
		if (lock) {
			throw new UnsupportedOperationException("NoOpLock can't unlock");
		}
	}

	public Condition newCondition() {
		throw new UnsupportedOperationException("NoOpLock can't provide a condition");
	}
}
