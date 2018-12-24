package scw.timer;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import scw.common.RunnableStaticProxy;
import scw.common.utils.XUtils;
import scw.locks.Lock;
import scw.locks.LockFactory;

public class AbstractScheduledExecutorService implements ScheduledExecutorService {
	private final java.util.concurrent.ScheduledExecutorService service;
	private final LockFactory lockFactory;

	public AbstractScheduledExecutorService(int corePoolSize, LockFactory lockFactory) {
		this.service = Executors.newScheduledThreadPool(corePoolSize);
		this.lockFactory = lockFactory;
	}

	public void scheduleAtFixedRate(String key, Runnable command, long initialDelay, long period, TimeUnit unit) {
		service.scheduleAtFixedRate(new Task(command, lockFactory), initialDelay, period, unit);
	}

	public void schedule(Runnable command, long delay, TimeUnit unit) {
		service.schedule(new Task(command, lockFactory), delay, unit);

	}

	public void scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
		service.scheduleWithFixedDelay(new Task(command, lockFactory), initialDelay, delay, unit);
	}

	public void scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
		service.scheduleAtFixedRate(new Task(command, lockFactory), initialDelay, period, unit);
	}
}

class Task extends RunnableStaticProxy {
	private Lock lock;

	public Task(Runnable command, LockFactory lockFactory) {
		super(command);
		this.lock = lockFactory.getLock(this.getClass().getName() + "#" + XUtils.getUUID());
	}

	@Override
	protected boolean before() {
		return lock.lock();
	}

	@Override
	protected void after() {
		lock.unlock();
	}
}
