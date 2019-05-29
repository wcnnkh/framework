package scw.utils.id.db;

import scw.core.utils.ClassUtils;
import scw.data.memcached.Memcached;
import scw.utils.locks.Lock;
import scw.utils.locks.MemcachedLock;

public final class MemcachedTableIdFactory extends AbstractTableIdFactory {
	private final Memcached memcached;

	public MemcachedTableIdFactory(scw.sql.orm.SelectMaxId db, Memcached memcached) {
		super(db);
		this.memcached = memcached;
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
			Lock lock = new MemcachedLock(memcached, key + "&lock");
			try {
				lock.lockWait();

				if (memcached.get(key) == null) {
					long maxId = getMaxId(tableClass, fieldName);
					return memcached.incr(key, 1, maxId + 1);
				}
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			} finally {
				lock.unlock();
			}
		}
		return memcached.incr(key, 1);
	}

}
