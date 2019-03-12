package scw.utils.id.db;

import scw.common.exception.ShuChaoWenRuntimeException;
import scw.common.utils.ClassUtils;
import scw.locks.Lock;
import scw.locks.MemcachedLock;
import scw.memcached.Memcached;

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
				throw new ShuChaoWenRuntimeException(e);
			} finally {
				lock.unlock();
			}
		}
		return memcached.incr(key, 1);
	}

}
