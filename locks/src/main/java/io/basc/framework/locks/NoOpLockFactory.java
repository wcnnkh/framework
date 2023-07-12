package io.basc.framework.locks;

import java.util.concurrent.locks.Lock;

public class NoOpLockFactory implements LockFactory {
	/**
	 * 无锁的
	 */
	public static final NoOpLockFactory NO = new NoOpLockFactory(false);
	/**
	 * 死锁
	 */
	public static final NoOpLockFactory DEAD = new NoOpLockFactory(true);

	private final boolean lock;

	public NoOpLockFactory(boolean lock) {
		this.lock = lock;
	}

	@Override
	public Lock getLock(String name) {
		return lock ? NoOpLock.DEAD : NoOpLock.NO;
	}

}
