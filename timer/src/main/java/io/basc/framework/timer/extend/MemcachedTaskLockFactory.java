package io.basc.framework.timer.extend;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.core.Ordered;
import io.basc.framework.memcached.Memcached;
import io.basc.framework.timer.support.CASTaskLockFactory;

@Provider(order = Ordered.LOWEST_PRECEDENCE - 2)
public class MemcachedTaskLockFactory extends CASTaskLockFactory {

	public MemcachedTaskLockFactory(Memcached memcached) {
		super(memcached);
	}

}
