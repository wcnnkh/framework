package scw.locks;

import java.util.concurrent.TimeUnit;

import scw.beans.annotation.AutoImpl;

@AutoImpl({ MemcachedLockFactory.class, RedisLockFactory.class, JdkLockFactory.class })
public interface LockFactory {
	Lock getLock(String name);

	Lock getLock(String name, long timeout, TimeUnit timeUnit);
}
