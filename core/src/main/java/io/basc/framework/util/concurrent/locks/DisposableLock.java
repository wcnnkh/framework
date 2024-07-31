package io.basc.framework.util.concurrent.locks;

import java.util.concurrent.TimeUnit;

import io.basc.framework.logger.Logger;
import io.basc.framework.logger.LoggerFactory;

public abstract class DisposableLock extends AbstractLock {
	private static Logger logger = LoggerFactory.getLogger(DisposableLock.class);

	@Override
	public void lockInterruptibly() throws InterruptedException {
		if (tryLock()) {
			return;
		}

		logger.warn("Unable to obtain lock, thread[{}] will permanently sleep", Thread.currentThread());
		// 永久休眠
		while (true) {
			Thread.sleep(Long.MAX_VALUE);
		}
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		if (tryLock()) {
			return true;
		}
		return false;
	}
}
