package run.soeasy.framework.util.concurrent.locks;

import java.util.concurrent.TimeUnit;

public interface DisposableLock extends Lockable {

	@Override
	default void lockInterruptibly() throws InterruptedException, DisposableLockException {
		if (tryLock()) {
			return;
		}

		// 一个一次性的锁如果拿不到锁说明永远也拿不到了
		throw new DisposableLockException("Unable to obtain lock");
	}

	@Override
	default boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		if (tryLock()) {
			return true;
		}
		// 一个一次性的锁如果拿不到锁也不用等了，不会再拿到了
		return false;
	}
}
