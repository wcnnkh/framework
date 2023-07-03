package io.basc.framework.timer.extend;

import io.basc.framework.memcached.Memcached;
import io.basc.framework.timer.support.CASTaskLockFactory;

public class MemcachedTaskLockFactory extends CASTaskLockFactory {

	public MemcachedTaskLockFactory(Memcached memcached) {
		super(memcached);
	}

}
