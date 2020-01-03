package scw.locks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class JdkLockFactory extends AbstractLockFactory {
	private final ConcurrentHashMap<String, JdkLock> lockMap = new ConcurrentHashMap<String, JdkLock>();

	public Lock getLock(String name, long timeout, TimeUnit timeUnit) {
		JdkLock lock = new JdkLock(name);
		JdkLock old = lockMap.putIfAbsent(name, lock);
		return old == null ? lock : old;
	}

	private final class JdkLock extends AbstractLock {
		private AtomicBoolean atomicBoolean = new AtomicBoolean(false);
		private String name;

		public JdkLock(String name) {
			this.name = name;
		}

		public boolean tryLock() {
			return atomicBoolean.compareAndSet(false, true);
		}

		public void unlock() {
			if (atomicBoolean.compareAndSet(true, false)) {
				lockMap.remove(name);
			}
		}
	}
}
