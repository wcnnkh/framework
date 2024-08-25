package io.basc.framework.util.concurrent.locks;

public final class NoOpLock implements DisposableLock {
	/**
	 * 无锁的
	 */
	public static final NoOpLock NO = new NoOpLock(false);
	/**
	 * 死锁
	 */
	public static final NoOpLock DEAD = new NoOpLock(true);

	private final boolean lock;

	private NoOpLock(boolean lock) {
		this.lock = lock;
	}

	public boolean tryLock() {
		return !lock;
	}

	public void unlock() {
		if (lock) {
			throw new UnsupportedOperationException("NoOpLock can't unlock");
		}
	}
}
