package scw.locks;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import scw.env.SystemEnvironment;

public abstract class AbstractLock implements Lock{
	private static final long DEFAULT_SLEEP_TIME = SystemEnvironment
			.getInstance().getValue("lock.sleep.time", Long.class, 1L);
	
	private long sleepTime = DEFAULT_SLEEP_TIME;
	
	public long getSleepTime() {
		return sleepTime;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public boolean tryLock(long period, TimeUnit timeUnit)
			throws InterruptedException {
		boolean b = false;
		while (!(b = tryLock())) {
			timeUnit.sleep(period);
		}
		return b;
	}

	/**
	 * 默认为ms试一次
	 */
	public void lock() {
		try {
			lockInterruptibly();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void lockInterruptibly() throws InterruptedException {
		while (!tryLock()) {
			TimeUnit.MILLISECONDS.sleep(sleepTime);
		}
	}
	
	public Condition newCondition() {
		throw new UnsupportedOperationException();
	}
}
