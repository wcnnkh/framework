package scw.data.memcached.x;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import scw.data.memcached.CAS;

public abstract class AbstractXMemcached implements scw.data.memcached.Memcached {
	public abstract MemcachedClient getMemcachedClient();

	public <T> T get(String key) {
		try {
			return getMemcachedClient().get(key);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public <T> CAS<T> gets(String key) {
		GetsResponse<T> cas;
		try {
			cas = getMemcachedClient().gets(key);
			if (cas == null) {
				return null;
			}
			return new CAS<T>(cas.getCas(), cas.getValue());
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean set(String key, Object value) {
		try {
			return getMemcachedClient().set(key, 0, value);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean set(String key, int exp, Object data) {
		try {
			return getMemcachedClient().set(key, exp, data);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean add(String key, Object value) {
		try {
			return getMemcachedClient().add(key, 0, value);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean add(String key, int exp, Object data) {
		try {
			return getMemcachedClient().add(key, exp, data);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean cas(String key, Object data, long cas) {
		try {
			return getMemcachedClient().cas(key, 0, data, cas);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean cas(String key, int exp, Object data, long cas) {
		try {
			return getMemcachedClient().cas(key, exp, data, cas);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public <T> T getAndTouch(String key, int newExp) {
		// 因为可能不支持此协议
		T t;
		try {
			t = getMemcachedClient().get(key);
			if (t != null) {
				getMemcachedClient().set(key, newExp, t);
			}
			return t;
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean touch(String key, int exp) {
		try {
			return getMemcachedClient().touch(key, exp);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public <T> Map<String, T> get(Collection<String> keyCollections) {
		try {
			return getMemcachedClient().get(keyCollections);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public long incr(String key, long incr) {
		try {
			return getMemcachedClient().incr(key, incr);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public long decr(String key, long decr) {
		try {
			return getMemcachedClient().incr(key, decr);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public long incr(String key, long incr, long initValue) {
		try {
			return getMemcachedClient().incr(key, incr, initValue);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public long decr(String key, long decr, long initValue) {
		try {
			return getMemcachedClient().incr(key, decr, initValue);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public <T> Map<String, CAS<T>> gets(Collection<String> keyCollections) {
		Map<String, GetsResponse<T>> map = null;
		try {
			map = getMemcachedClient().gets(keyCollections);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}

		if (map != null) {
			Map<String, CAS<T>> casMap = new HashMap<String, CAS<T>>();
			for (Entry<String, GetsResponse<T>> entry : map.entrySet()) {
				casMap.put(entry.getKey(), new CAS<T>(entry.getValue().getCas(), entry.getValue().getValue()));
			}
			return casMap;
		}
		return null;
	}

	public boolean delete(String key) {
		try {
			return getMemcachedClient().delete(key);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean delete(String key, long cas, long opTimeout) {
		try {
			return getMemcachedClient().delete(key, cas, opTimeout);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}
}
