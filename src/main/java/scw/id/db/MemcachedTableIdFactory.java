package scw.id.db;

import scw.core.utils.ClassUtils;
import scw.data.memcached.Memcached;
import scw.locks.Lock;
import scw.locks.LockFactory;
import scw.locks.MemcachedLockFactory;
import scw.sql.orm.ORMOperations;

public final class MemcachedTableIdFactory extends AbstractTableIdFactory {
	private final Memcached memcached;
	private final LockFactory lockFactory;

	public MemcachedTableIdFactory(ORMOperations db, Memcached memcached) {
		super(db);
		this.memcached = memcached;
		this.lockFactory = new MemcachedLockFactory(memcached);
	}

	private String getCacheKey(Class<?> tableClass, String fieldName) {
		StringBuilder sb = new StringBuilder(64);
		sb.append(this.getClass().getName());
		sb.append("&");
		sb.append(ClassUtils.getProxyRealClassName(tableClass));
		sb.append("&");
		sb.append(fieldName);
		return sb.toString();
	}

	public long generator(Class<?> tableClass, String fieldName) {
		String key = getCacheKey(tableClass, fieldName);
		if (memcached.get(key) == null) {
			// 不存在
			Lock lock = lockFactory.getLock(key + "&lock");
			try {
				lock.lock();

				if (memcached.get(key) == null) {
					long maxId = getMaxId(tableClass, fieldName);
					return memcached.incr(key, 1, maxId + 1);
				}
			} finally {
				lock.unlock();
			}
		}
		return memcached.incr(key, 1);
	}

}
