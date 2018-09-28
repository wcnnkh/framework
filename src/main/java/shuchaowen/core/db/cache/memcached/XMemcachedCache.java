package shuchaowen.core.db.cache.memcached;

import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import shuchaowen.core.db.cache.Cache;
import shuchaowen.core.db.cache.CacheUtils;
import shuchaowen.core.util.XTime;

/**
 * 推荐使用此方式实现简单缓存
 * 
 * @author shuchaowen
 *
 */
public class XMemcachedCache implements Cache {
	private MemcachedClient memcachedClient;
	private String prefix;
	private int exp;// 过期时间

	/**
	 * 热点数据 过期时间7天
	 * 
	 * @param memcachedClient
	 */
	public XMemcachedCache(MemcachedClient memcachedClient) {
		this((int) ((7 * XTime.ONE_DAY) / 1000), memcachedClient);
	}

	public XMemcachedCache(int exp, MemcachedClient memcachedClient) {
		this("", exp, memcachedClient);
	}

	public XMemcachedCache(String prefix, int exp, MemcachedClient memcachedClient) {
		this.prefix = prefix;
		this.exp = exp;
		this.memcachedClient = memcachedClient;
	}

	public <T> T getById(Class<T> type, String tableName, Object... params) {
		String key = prefix + CacheUtils.getObjectKey(type, params);
		T t = null;
		try {
			t = memcachedClient.get(key);
		} catch (TimeoutException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (MemcachedException e1) {
			e1.printStackTrace();
		}

		if (t == null) {
			return t;
		}

		if (exp > 0) {
			try {
				memcachedClient.set(key, exp, t);
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (MemcachedException e) {
				e.printStackTrace();
			}
		}
		return t;
	}

	public void save(Object bean) {
		// ignore
	}

	public void update(Object bean) {
		delete(bean);
	}

	public void delete(Object bean) {
		try {
			memcachedClient.delete(prefix + CacheUtils.getObjectKey(bean));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveOrUpdate(Object bean) {
		delete(bean);
	}
}
