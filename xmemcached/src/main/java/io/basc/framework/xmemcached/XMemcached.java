package io.basc.framework.xmemcached;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.CAS;
import io.basc.framework.memcached.Memcached;
import io.basc.framework.util.Assert;
import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;

@Provider
public final class XMemcached implements Memcached {
	private final MemcachedClient memcachedClient;
	private volatile boolean isSupportTouch = true;// 是否支持touch协议

	public XMemcached(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

	public Object get(String key) {
		try {
			return memcachedClient.get(key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CAS<Object> gets(String key) {
		GetsResponse<Object> cas;
		try {
			cas = memcachedClient.gets(key);
			if (cas == null) {
				return null;
			}

			return new CAS<>(cas.getCas(), cas.getValue());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void set(String key, Object value, TypeDescriptor valueType) {
		try {
			memcachedClient.set(key, 0, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void checkExp(long exp) {
		Assert.requiredArgument(exp > Integer.MAX_VALUE, "exp should be less than or equal to " + Integer.MAX_VALUE);
	}

	@Override
	public void set(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		checkExp(exp);
		try {
			memcachedClient.set(key, (int) expUnit.toSeconds(exp), value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean setIfAbsent(String key, Object value, TypeDescriptor valueType) {
		if (value == null) {
			return false;
		}

		try {
			return memcachedClient.add(key, 0, value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean setIfAbsent(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		if (value == null) {
			return false;
		}

		checkExp(exp);
		try {
			return memcachedClient.add(key, (int) expUnit.toSeconds(exp), value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean cas(String key, Object value, TypeDescriptor valueType, long cas, long exp, TimeUnit expUnit) {
		if (value == null) {
			return false;
		}

		try {
			return memcachedClient.cas(key, (int) expUnit.toSeconds(exp), value, cas);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T privateGetAndTouch(String key, long newExp) {
		Object v;
		checkExp(newExp);
		try {
			v = memcachedClient.get(key);
			if (v == null) {
				return null;
			}

			if (v != null) {
				memcachedClient.set(key, (int) newExp, v);
			}

			return (T) v;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Object getAndTouch(String key, long newExp, TimeUnit expUnit) {
		checkExp(newExp);
		if (isSupportTouch) {
			try {
				return memcachedClient.getAndTouch(key, (int) newExp);
			} catch (net.rubyeye.xmemcached.exception.MemcachedException e) {// 不支持touch协议
				isSupportTouch = false;
				return privateGetAndTouch(key, newExp);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			return privateGetAndTouch(key, newExp);
		}
	}

	@Override
	public boolean touch(String key, long exp, TimeUnit expUnit) {
		checkExp(exp);
		if (isSupportTouch) {
			try {
				return memcachedClient.touch(key, (int) expUnit.toSeconds(exp));
			} catch (net.rubyeye.xmemcached.exception.MemcachedException e) {// 不支持touch协议
				isSupportTouch = false;
				getAndTouch(key, exp, expUnit);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else {
			getAndTouch(key, exp, expUnit);
		}
		return true;
	}

	@Override
	public Map<String, Object> get(Collection<String> keys) {
		try {
			return memcachedClient.get(keys);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public long incr(String key, long delta) {
		try {
			return memcachedClient.incr(key, delta);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public long decr(String key, long delta) {
		try {
			return memcachedClient.incr(key, delta);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public long incr(String key, long delta, long initValue) {
		try {
			return memcachedClient.incr(key, delta, initValue);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public long decr(String key, long delta, long initValue) {
		try {
			return memcachedClient.incr(key, delta, initValue);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Map<String, CAS<Object>> gets(Collection<String> keyCollections) {
		Map<String, GetsResponse<Object>> map = null;
		try {
			map = memcachedClient.gets(keyCollections);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		if (map != null) {
			Map<String, CAS<Object>> casMap = new HashMap<String, CAS<Object>>();
			for (Entry<String, GetsResponse<Object>> entry : map.entrySet()) {
				GetsResponse<Object> v = entry.getValue();
				casMap.put(entry.getKey(), new CAS<Object>(v.getCas(), v.getValue()));
			}
			return casMap;
		}
		return null;
	}

	public boolean delete(String key) {
		try {
			return memcachedClient.delete(key);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean delete(String key, long cas) {
		try {
			return memcachedClient.delete(key, cas, memcachedClient.getOpTimeout());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean exists(String key) {
		return get(key) != null;
	}

	public long incr(String key, long delta, long initValue, long exp, TimeUnit expUnit) {
		checkExp(exp);
		try {
			return memcachedClient.incr(key, delta, initValue, memcachedClient.getOpTimeout(),
					(int) expUnit.toSeconds(exp));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public long decr(String key, long delta, long initValue, long exp, TimeUnit expUnit) {
		checkExp(exp);
		try {
			return memcachedClient.decr(key, delta, initValue, memcachedClient.getOpTimeout(),
					(int) expUnit.toMillis(exp));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean expire(String key, long exp, TimeUnit expUnit) {
		return touch(key, exp, expUnit);
	}
}
