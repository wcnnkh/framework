package io.basc.framework.xmemcached;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.basc.framework.convert.TypeDescriptor;
import io.basc.framework.data.CAS;
import io.basc.framework.memcached.Memcached;
import io.basc.framework.memcached.config.MemcachedNodeProperties;
import io.basc.framework.memcached.config.MemcachedProperties;
import io.basc.framework.util.Assert;
import io.basc.framework.util.CollectionUtils;
import net.rubyeye.xmemcached.GetsResponse;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.exception.MemcachedException;

public final class XMemcached implements Memcached {
	private final MemcachedClient memcachedClient;

	public XMemcached(XMemcachedProperties properties) {
		XMemcachedClientBuilder builder = builder(properties);
		try {
			this.memcachedClient = builder.build();
		} catch (IOException e) {
			throw new io.basc.framework.memcached.MemcachedException(properties.toString(), e);
		}
	}

	public XMemcached(MemcachedClient memcachedClient) {
		this.memcachedClient = memcachedClient;
	}

	public Object get(String key) {
		try {
			return memcachedClient.get(key);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	public CAS<Object> gets(String key) {
		GetsResponse<Object> cas;
		try {
			cas = memcachedClient.gets(key);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
		if (cas == null) {
			return null;
		}

		return new CAS<>(cas.getCas(), cas.getValue());
	}

	@Override
	public void set(String key, Object value, TypeDescriptor valueType) {
		try {
			memcachedClient.set(key, 0, value);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	private int getExp(long exp, TimeUnit expUnit) {
		long second = expUnit.toSeconds(exp);
		Assert.requiredArgument(second >= 0 && second <= Integer.MAX_VALUE,
				"exp should be less than or equal to " + Integer.MAX_VALUE);
		if (exp > 0 && second == 0) {
			// memcached过期时间的最小单位是秒
			return 1;
		} else {
			return (int) second;
		}
	}

	@Override
	public void set(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		try {
			memcachedClient.set(key, getExp(exp, expUnit), value);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	@Override
	public boolean setIfAbsent(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		if (value == null) {
			return false;
		}

		try {
			return memcachedClient.add(key, getExp(exp, expUnit), value);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	/**
	 * 无原子性，分两步执行的
	 */
	@Override
	public boolean setIfPresent(String key, Object value, TypeDescriptor valueType, long exp, TimeUnit expUnit) {
		if (value == null) {
			return false;
		}

		if (!exists(key)) {
			return false;
		}

		set(key, value, valueType, exp, expUnit);
		return true;
	}

	@Override
	public boolean cas(String key, Object value, TypeDescriptor valueType, long cas, long exp, TimeUnit expUnit) {
		if (value == null) {
			return false;
		}

		try {
			return memcachedClient.cas(key, getExp(exp, expUnit), value, cas);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	public Object getAndTouch(String key, long newExp, TimeUnit expUnit) {
		try {
			return memcachedClient.getAndTouch(key, getExp(newExp, expUnit));
		} catch (TimeoutException | InterruptedException | net.rubyeye.xmemcached.exception.MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	@Override
	public boolean touch(String key, long exp, TimeUnit expUnit) {
		try {
			return memcachedClient.touch(key, getExp(exp, expUnit));
		} catch (TimeoutException | InterruptedException | net.rubyeye.xmemcached.exception.MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	@Override
	public Map<String, Object> get(Collection<String> keys) {
		if (keys == null || keys.isEmpty()) {
			return Collections.emptyMap();
		}

		try {
			return memcachedClient.get(keys);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(keys.toString(), e);
		}
	}

	public long incr(String key, long delta) {
		try {
			return memcachedClient.incr(key, delta);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	public long decr(String key, long delta) {
		try {
			return memcachedClient.incr(key, delta);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	public long incr(String key, long delta, long initValue) {
		try {
			return memcachedClient.incr(key, delta, initValue);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	public long decr(String key, long delta, long initValue) {
		try {
			return memcachedClient.incr(key, delta, initValue);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	public Map<String, CAS<Object>> gets(Collection<String> keyCollections) {
		if (CollectionUtils.isEmpty(keyCollections)) {
			return Collections.emptyMap();
		}

		Map<String, GetsResponse<Object>> map = null;
		try {
			map = memcachedClient.gets(keyCollections);
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(keyCollections.toString(), e);
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
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	public boolean delete(String key, long cas) {
		try {
			return memcachedClient.delete(key, cas, memcachedClient.getOpTimeout());
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	public boolean exists(String key) {
		return get(key) != null;
	}

	public long incr(String key, long delta, long initValue, long exp, TimeUnit expUnit) {
		try {
			return memcachedClient.incr(key, delta, initValue, memcachedClient.getOpTimeout(), getExp(exp, expUnit));
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	public long decr(String key, long delta, long initValue, long exp, TimeUnit expUnit) {
		try {
			return memcachedClient.decr(key, delta, initValue, memcachedClient.getOpTimeout(), getExp(exp, expUnit));
		} catch (TimeoutException | InterruptedException | MemcachedException e) {
			throw new io.basc.framework.memcached.MemcachedException(key, e);
		}
	}

	@Override
	public boolean expire(String key, long exp, TimeUnit expUnit) {
		return touch(key, exp, expUnit);
	}

	@Override
	public boolean setIfPresent(String key, Object value, TypeDescriptor valueType) {
		return setIfPresent(key, value, valueType, 0, TimeUnit.SECONDS);
	}

	@Override
	public boolean setIfAbsent(String key, Object value, TypeDescriptor valueType) {
		return setIfAbsent(key, value, valueType, 0, TimeUnit.SECONDS);
	}

	@Override
	public boolean cas(String key, Object value, TypeDescriptor valueType, long cas) {
		return cas(key, value, valueType, cas, 0, TimeUnit.SECONDS);
	}

	public static XMemcachedClientBuilder builder(XMemcachedProperties memcachedProperties) {
		XMemcachedClientBuilder builder;
		if (CollectionUtils.isEmpty(memcachedProperties.getNodes())) {
			builder = new XMemcachedClientBuilder();
		} else {
			Map<InetSocketAddress, InetSocketAddress> addressMap = new LinkedHashMap<>();
			List<Integer> weights = new ArrayList<Integer>();
			for (MemcachedNodeProperties node : memcachedProperties.getNodes()) {
				InetSocketAddress master = new InetSocketAddress(node.getHost(), node.getPort());
				MemcachedProperties slaveProperties = node.getSlave();
				InetSocketAddress slave = null;
				if (slaveProperties != null) {
					slave = new InetSocketAddress(node.getHost(), node.getPort());
				}
				weights.add(node.getWeight());
				addressMap.put(master, slave);
			}
			builder = new XMemcachedClientBuilder(addressMap, weights.stream().mapToInt(Integer::intValue).toArray());
		}
		// 宕机报警
		builder.setFailureMode(memcachedProperties.isFailureMode());
		// 使用二进制文件
		if (memcachedProperties.getCommandFactory() != null) {
			builder.setCommandFactory(memcachedProperties.getCommandFactory());
		}
		return builder;
	}
}
