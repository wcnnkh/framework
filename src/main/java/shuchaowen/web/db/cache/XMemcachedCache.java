package shuchaowen.web.db.cache;

import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.GetsResponse;
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
	 * 热点数据  过期时间7天
	 * @param memcachedClient
	 */
	public XMemcachedCache(MemcachedClient memcachedClient) {
		this((int)((7 * XTime.ONE_DAY)/1000), memcachedClient);
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
		if (MemcachedCAS.class.isAssignableFrom(type)) {
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
		} else {
			GetsResponse<T> response = null;
			try {
				response = memcachedClient.gets(key);
			} catch (TimeoutException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (MemcachedException e) {
				e.printStackTrace();
			}
			if (response == null) {
				return null;
			}

			if (exp > 0) {
				try {
					memcachedClient.set(key, exp, response.getValue());
				} catch (TimeoutException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (MemcachedException e) {
					e.printStackTrace();
				}
			}

			T t = response.getValue();
			if (t instanceof MemcachedCAS) {
				((MemcachedCAS) t).setCas(exp > 0 ? response.getCas() + 1 : response.getCas());
			}
			return t;
		}
	}

	public void save(Object bean) {
		try {
			memcachedClient.add(prefix + CacheUtils.getObjectKey(bean), exp, bean);
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}

	public void update(Object bean) {
		String key = prefix + CacheUtils.getObjectKey(bean);
		try {
			if (bean instanceof MemcachedCAS) {
				boolean b = memcachedClient.cas(key, exp, bean, ((MemcachedCAS) bean).getCas());
				if (!b) {
					throw new CASException(key, ((MemcachedCAS) bean).getCas());
				}
			} else {
				memcachedClient.set(key, exp, bean);
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}

	public void delete(Object bean) {
		try {
			memcachedClient.delete(prefix + CacheUtils.getObjectKey(bean));
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}

	public void saveOrUpdate(Object bean) {
		String key = prefix + CacheUtils.getObjectKey(bean);
		try {
			if (bean instanceof MemcachedCAS && ((MemcachedCAS) bean).getCas() != 0) {
				boolean b = memcachedClient.cas(key, exp, bean, ((MemcachedCAS) bean).getCas());
				if (!b) {
					throw new CASException(key, ((MemcachedCAS) bean).getCas());
				}
			} else {
				memcachedClient.set(key, exp, bean);
			}
		} catch (TimeoutException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (MemcachedException e) {
			e.printStackTrace();
		}
	}
}
