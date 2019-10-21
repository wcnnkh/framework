package scw.locks;

import java.util.concurrent.TimeUnit;

public final class EmptyLock implements Lock {
	public final boolean lock;

	public EmptyLock(boolean lock) {
		this.lock = lock;
	}

	public boolean tryLock() {
		return lock;
	}

	public boolean tryLock(long period, TimeUnit timeUnit) throws InterruptedException {
		return lock;
	}

	public void lockInterruptibly() throws InterruptedException {
		// ignore
	}

	public void lock() {
		// ignore
	}

	public void unlock() {
		// ignore
	}

}
