package scw.data.memcached.x;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeoutException;

import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.exception.MemcachedException;
import scw.core.serializer.NoTypeSpecifiedSerializer;
import scw.core.utils.CollectionUtils;
import scw.data.memcached.AbstractMemcached;
import scw.data.memcached.CAS;

public abstract class AbstractXMemcached extends AbstractMemcached {

	public AbstractXMemcached(NoTypeSpecifiedSerializer serializer) {
		super(serializer);
	}

	public abstract MemcachedClient getMemcachedClient();

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		Object data;
		try {
			data = getMemcachedClient().get(key);
			if (data == null) {
				return null;
			}

			return (T) (serializer == null ? data : serializer.deserialize((byte[]) data));
		} catch (TimeoutException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (InterruptedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		} catch (MemcachedException e) {
			throw new scw.data.memcached.MemcachedException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T> CAS<T> gets(String key) {
		GetsResponse<Object> cas;
		try {
			cas = getMemcachedClient().gets(key);
			if (cas == null) {
				return null;
			}

			T v = (T) (serializer == null ? cas.getValue() : serializer.deserialize((byte[]) cas.getValue()));
			return new CAS<T>(cas.getCas(), v);
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

		Object v = serializer == null ? value : serializer.serialize(value);
		try {
			return getMemcachedClient().set(key, 0, v);
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

		Object v = serializer == null ? data : serializer.serialize(data);
		try {
			return getMemcachedClient().set(key, exp, v);
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

		Object v = serializer == null ? value : serializer.serialize(value);
		try {
			return getMemcachedClient().add(key, 0, v);
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

		Object v = serializer == null ? data : serializer.serialize(data);
		try {
			return getMemcachedClient().add(key, exp, v);
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

		Object v = serializer == null ? data : serializer.serialize(data);
		try {
			return getMemcachedClient().cas(key, 0, v, cas);
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

		Object v = serializer == null ? data : serializer.serialize(data);
		try {
			return getMemcachedClient().cas(key, exp, v, cas);
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
			v = getMemcachedClient().get(key);
			if (v == null) {
				return null;
			}

			if (v != null) {
				getMemcachedClient().set(key, newExp, v);
			}

			return (T) (serializer == null ? v : serializer.deserialize((byte[]) v));
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

	@SuppressWarnings("unchecked")
	public <T> Map<String, T> get(Collection<String> keyCollections) {
		if (serializer == null) {
			try {
				return getMemcachedClient().get(keyCollections);
			} catch (TimeoutException e) {
				throw new scw.data.memcached.MemcachedException(e);
			} catch (InterruptedException e) {
				throw new scw.data.memcached.MemcachedException(e);
			} catch (MemcachedException e) {
				throw new scw.data.memcached.MemcachedException(e);
			}
		} else {
			Map<String, byte[]> data;
			try {
				data = getMemcachedClient().get(keyCollections);
			} catch (TimeoutException e) {
				throw new scw.data.memcached.MemcachedException(e);
			} catch (InterruptedException e) {
				throw new scw.data.memcached.MemcachedException(e);
			} catch (MemcachedException e) {
				throw new scw.data.memcached.MemcachedException(e);
			}

			if (CollectionUtils.isEmpty(data)) {
				return null;
			}

			Map<String, T> valueMap = new LinkedHashMap<String, T>(data.size(), 1);
			for (Entry<String, byte[]> entry : data.entrySet()) {
				valueMap.put(entry.getKey(), (T) serializer.deserialize(entry.getValue()));
			}
			return valueMap;
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

	@SuppressWarnings("unchecked")
	public <T> Map<String, CAS<T>> gets(Collection<String> keyCollections) {
		Map<String, GetsResponse<Object>> map = null;
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
			for (Entry<String, GetsResponse<Object>> entry : map.entrySet()) {
				GetsResponse<Object> v = entry.getValue();
				T value = (T) (serializer == null ? v.getValue() : serializer.deserialize((byte[]) v.getValue()));
				casMap.put(entry.getKey(), new CAS<T>(v.getCas(), value));
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
