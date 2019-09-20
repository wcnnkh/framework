package scw.data.memcached.x;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import scw.data.cas.CAS;
import scw.data.cas.CASOperations;
import scw.data.memcached.Memcached;

public class XMemcachedImpl implements Memcached {
	private final MemcachedClient memcachedClient;
	private final CASOperations casOperations;

	public XMemcachedImpl(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
		this.casOperations = new XMemcachedCASOperations(memcachedClient);
	}

	public <T> T get(String key) {
		try {
			return memcachedClient.get(key);
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
			cas = memcachedClient.gets(key);
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
		if (value == null) {
			return false;
		}

		try {
			return memcachedClient.set(key, 0, value);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean set(String key, int exp, Object data) {
		if (data == null) {
			return false;
		}

		try {
			return memcachedClient.set(key, exp, data);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean add(String key, Object value) {
		if (value == null) {
			return false;
		}

		try {
			return memcachedClient.add(key, 0, value);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean add(String key, int exp, Object data) {
		if (data == null) {
			return false;
		}

		try {
			return memcachedClient.add(key, exp, data);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean cas(String key, Object data, long cas) {
		if (data == null) {
			return false;
		}

		try {
			return memcachedClient.cas(key, 0, data, cas);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean cas(String key, int exp, Object data, long cas) {
		if (data == null) {
			return false;
		}

		try {
			return memcachedClient.cas(key, exp, data, cas);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> T getAndTouch(String key, int newExp) {
		// 因为可能不支持此协议
		Object v;
		try {
			v = memcachedClient.get(key);
			if (v == null) {
				return null;
			}

			if (v != null) {
				memcachedClient.set(key, newExp, v);
			}

			return (T) v;
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
			return memcachedClient.touch(key, exp);
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
			return memcachedClient.get(keyCollections);
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
			return memcachedClient.incr(key, incr);
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
			return memcachedClient.incr(key, decr);
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
			return memcachedClient.incr(key, incr, initValue);
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
			return memcachedClient.incr(key, decr, initValue);
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
			map = memcachedClient.gets(keyCollections);
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
				GetsResponse<T> v = entry.getValue();
				casMap.put(entry.getKey(), new CAS<T>(v.getCas(), v.getValue()));
			}
			return casMap;
		}
		return null;
	}

	public boolean delete(String key) {
		try {
			return memcachedClient.delete(key);
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
			return memcachedClient.delete(key, cas, opTimeout);
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	public boolean isExist(String key) {
		return get(key) != null;
	}

	public CASOperations getCASOperations() {
		return casOperations;
	}
}
