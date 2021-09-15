package io.basc.framework.locks;

import java.util.concurrent.TimeUnit;

public abstract class RenewableLockFactory implements LockFactory{
	private TimeUnit timeUnit = TimeUnit.SECONDS;
	//默认10秒
	private long timeout = 10;
	
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}

	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
	
	public final RenewableLock getLock(String name){
		return getLock(name, getTimeUnit(), getTimeout());
	}
	
	public abstract RenewableLock getLock(String name, TimeUnit timeUnit, long timeout);
}
