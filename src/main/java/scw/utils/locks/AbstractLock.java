package scw.utils.locks;

import java.util.concurrent.TimeUnit;

public abstract class AbstractLock implements Lock {
	public void lockWait(long period, TimeUnit timeUnit)
			throws InterruptedException {
		while (!lock()) {
			timeUnit.sleep(period);
		}
	}

	/**
	 * 默认为5ms试一次
	 */
	public void lockWait() throws InterruptedException {
		lockWait(5, TimeUnit.MILLISECONDS);
	}
}
