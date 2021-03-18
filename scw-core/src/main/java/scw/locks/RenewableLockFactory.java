package scw.locks;

import java.util.concurrent.TimeUnit;

public abstract class RenewableLockFactory implements LockFactory{
	private TimeUnit timeUnit = TimeUnit.MINUTES;
	private long timeout = 5;
	
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
