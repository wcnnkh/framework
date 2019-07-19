package scw.data.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import scw.core.serializer.Serializer;
import scw.core.utils.Assert;
import scw.core.utils.CollectionUtils;
import scw.io.Bytes;

public abstract class AbstractSpecifiedTypeOperations<T> implements RedisOperations<String, T> {
	private final Class<T> type;

	protected abstract RedisOperations<byte[], byte[]> getBinaryOperations();

	protected abstract RedisOperations<String, String> getStringOperations();

	protected abstract Serializer getSerializer();

	public AbstractSpecifiedTypeOperations(Class<T> type) {
		this.type = type;
	}

	public T get(String key) {
		byte[] data = getBinaryOperations().get(Bytes.string2bytes(key));
		return data == null ? null : getSerializer().deserialize(type, data);
	}

	public List<T> mget(String... keys) {
		if (keys == null) {
			return null;
		}

		byte[][] ks = Bytes.string2bytes(keys);
		List<byte[]> list = getBinaryOperations().mget(ks);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		List<T> dataList = new ArrayList<T>(keys.length);
		for (byte[] data : list) {
			dataList.add(data == null ? null : getSerializer().deserialize(type, data));
		}
		return dataList;
	}

	public void set(String key, T value) {
		if (key == null) {
			return;
		}

		getBinaryOperations().set(Bytes.string2bytes(key), getSerializer().serialize(type, value));
	}

	public boolean setnx(String key, T value) {
		if (key == null) {
			return false;
		}

		return getBinaryOperations().setnx(Bytes.string2bytes(key), getSerializer().serialize(type, value));
	}

	public void setex(String key, int seconds, T value) {
		if (key == null) {
			return;
		}

		getBinaryOperations().setex(Bytes.string2bytes(key), seconds, getSerializer().serialize(type, value));
	}

	public boolean exists(String key) {
		if (key == null) {
			return false;
		}

		return getBinaryOperations().exists(Bytes.string2bytes(key));
	}

	public Long expire(String key, int seconds) {
		if (key == null) {
			return -1L;
		}

		return getBinaryOperations().expire(Bytes.string2bytes(key), seconds);
	}

	public boolean del(String key) {
		if (key == null) {
			return false;
		}

		return getBinaryOperations().del(Bytes.string2bytes(key));
	}

	public Long hset(String key, String field, T value) {
		if (key == null || field == null || value == null) {
			return -1L;
		}

		return getBinaryOperations().hset(Bytes.string2bytes(key), Bytes.string2bytes(field),
				getSerializer().serialize(type, value));
	}

	public Long hsetnx(String key, String field, T value) {
		if (key == null || field == null || value == null) {
			return -1L;
		}

		return getBinaryOperations().hsetnx(Bytes.string2bytes(key), Bytes.string2bytes(field),
				getSerializer().serialize(type, value));
	}

	public Long hdel(String key, String... fields) {
		if (key == null || fields == null) {
			return -1L;
		}

		return getBinaryOperations().hdel(Bytes.string2bytes(key), Bytes.string2bytes(fields));
	}

	public Long hlen(String key) {
		if (key == null) {
			return -1L;
		}

		return getBinaryOperations().hlen(Bytes.string2bytes(key));
	}

	public Boolean hexists(String key, String field) {
		if (key == null || field == null) {
			return false;
		}

		return getBinaryOperations().hexists(Bytes.string2bytes(key), Bytes.string2bytes(field));
	}

	public Long ttl(String key) {
		if (key == null) {
			return -1L;
		}

		return getBinaryOperations().ttl(Bytes.string2bytes(key));
	}

	public Long incr(String key) {
		if (key == null) {
			return -1L;
		}

		return getBinaryOperations().incr(Bytes.string2bytes(key));
	}

	public Long decr(String key) {
		if (key == null) {
			return -1L;
		}

		return getBinaryOperations().decr(Bytes.string2bytes(key));
	}

	public Collection<T> hvals(String key) {
		if (key == null) {
			return null;
		}

		Collection<byte[]> list = getBinaryOperations().hvals(Bytes.string2bytes(key));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		List<T> dataList = new ArrayList<T>(list.size());
		for (byte[] data : list) {
			dataList.add(data == null ? null : getSerializer().deserialize(type, data));
		}
		return dataList;
	}

	public T hget(String key, String field) {
		if (key == null || field == null) {
			return null;
		}

		byte[] data = getBinaryOperations().hget(Bytes.string2bytes(key), Bytes.string2bytes(field));
		return data == null ? null : getSerializer().deserialize(type, data);
	}

	public Collection<T> hmget(String key, String... fields) {
		if (key == null || fields == null) {
			return null;
		}

		Collection<byte[]> list = getBinaryOperations().hmget(Bytes.string2bytes(key), Bytes.string2bytes(fields));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		List<T> valueList = new ArrayList<T>(list.size());
		for (byte[] data : list) {
			valueList.add(data == null ? null : getSerializer().deserialize(type, data));
		}
		return valueList;
	}

	private byte[][] objectsSerialize(T... values) {
		byte[][] bs = new byte[values.length][];
		for (int i = 0; i < values.length; i++) {
			T v = values[i];
			if (v == null) {
				continue;
			}

			bs[i] = getSerializer().serialize(type, v);
		}
		return bs;
	}

	public Long lpush(String key, T... values) {
		if (key == null || values == null) {
			return -1L;
		}

		return getBinaryOperations().lpush(Bytes.string2bytes(key), objectsSerialize(values));
	}

	public Long rpush(String key, T... values) {
		if (key == null || values == null) {
			return -1L;
		}

		return getBinaryOperations().rpush(Bytes.string2bytes(key), objectsSerialize(values));
	}

	public T rpop(String key) {
		if (key == null) {
			return null;
		}

		byte[] data = getBinaryOperations().rpop(Bytes.string2bytes(key));
		return data == null ? null : getSerializer().deserialize(type, data);
	}

	public T lpop(String key) {
		if (key == null) {
			return null;
		}

		byte[] data = getBinaryOperations().lpop(Bytes.string2bytes(key));
		return data == null ? null : getSerializer().deserialize(type, data);
	}

	public Set<T> smembers(String key) {
		if (key == null) {
			return null;
		}

		Set<byte[]> set = getBinaryOperations().smembers(Bytes.string2bytes(key));
		if (CollectionUtils.isEmpty(set)) {
			return null;
		}

		Set<T> hashSet = new HashSet<T>(set.size(), 1);
		Iterator<byte[]> iterator = set.iterator();
		while (iterator.hasNext()) {
			byte[] data = iterator.next();
			hashSet.add(data == null ? null : getSerializer().deserialize(type, data));
		}
		return hashSet;
	}

	public Long srem(String key, T... members) {
		if (key == null || members == null) {
			return -1L;
		}

		return getBinaryOperations().srem(Bytes.string2bytes(key), objectsSerialize(members));
	}

	public Long sadd(String key, T... members) {
		if (key == null || members == null) {
			return -1L;
		}

		return getBinaryOperations().sadd(Bytes.string2bytes(key), objectsSerialize(members));
	}

	public Long zadd(String key, long score, T member) {
		if (key == null || member == null) {
			return -1L;
		}

		return getBinaryOperations().zadd(Bytes.string2bytes(key), score, getSerializer().serialize(type, member));
	}

	public Boolean set(String key, T value, String nxxx, String expx, long time) {
		if (key == null || value == null || nxxx == null || expx == null) {
			return false;
		}

		return getBinaryOperations().set(Bytes.string2bytes(key), getSerializer().serialize(type, value),
				Bytes.string2bytes(nxxx), Bytes.string2bytes(expx), time);
	}

	public Boolean sIsMember(String key, T member) {
		if (key == null || member == null) {
			return false;
		}

		return getBinaryOperations().sIsMember(Bytes.string2bytes(key), getSerializer().serialize(type, member));
	}

	public T lindex(String key, int index) {
		if (key == null) {
			return null;
		}

		byte[] data = getBinaryOperations().lindex(Bytes.string2bytes(key), index);
		return data == null ? null : getSerializer().deserialize(type, data);
	}

	public Long llen(String key) {
		if (key == null) {
			return -1L;
		}
		return getBinaryOperations().llen(Bytes.string2bytes(key));
	}

	public Object eval(String script, List<String> keys, List<T> args) {
		if (script == null) {
			return null;
		}

		List<byte[]> ks = null;
		if (!CollectionUtils.isEmpty(keys)) {
			ks = new ArrayList<byte[]>(keys.size());
			Iterator<String> iterator = keys.iterator();
			while (iterator.hasNext()) {
				ks.add(Bytes.string2bytes(iterator.next()));
			}
		}

		List<byte[]> bs = null;
		if (!CollectionUtils.isEmpty(args)) {
			bs = new ArrayList<byte[]>(args.size());
			Iterator<T> iterator = args.iterator();
			while (iterator.hasNext()) {
				T v = iterator.next();
				bs.add(v == null ? null : getSerializer().serialize(type, v));
			}
		}

		return getBinaryOperations().eval(Bytes.string2bytes(script), ks, bs);
	}

	private Map<String, T> mapDeSerizale(Map<byte[], byte[]> map) {
		if (CollectionUtils.isEmpty(map)) {
			return null;
		}

		Map<String, T> valueMap = new LinkedHashMap<String, T>(map.size(), 1);
		for (Entry<byte[], byte[]> entry : map.entrySet()) {
			byte[] data = entry.getValue();
			valueMap.put(new String(Bytes.bytes2chars(data)),
					data == null ? null : getSerializer().deserialize(type, data));
		}
		return valueMap;
	}

	public Map<String, T> hgetAll(String key) {
		if (key == null) {
			return null;
		}

		Map<byte[], byte[]> map = getBinaryOperations().hgetAll(Bytes.string2bytes(key));
		return mapDeSerizale(map);
	}

	private List<T> popObjectResponse(List<byte[]> list) {
		int i = 0;
		byte[] v1 = list.get(i++);
		if (v1 != null) {
			return null;
		}

		List<T> valueList = new ArrayList<T>(list.size());
		for (int size = list.size(); i < size; i++) {
			byte[] v = list.get(i);
			if (v == null) {
				continue;
			}

			valueList.add(getSerializer().deserialize(type, v));
		}
		return valueList;
	}

	public List<T> brpop(int timeout, String key) {
		if (key == null) {
			return null;
		}

		List<byte[]> list = getBinaryOperations().brpop(timeout, Bytes.string2bytes(key));
		return popObjectResponse(list);
	}

	public List<T> blpop(int timeout, String key) {
		if (key == null) {
			return null;
		}

		List<byte[]> list = getBinaryOperations().blpop(timeout, Bytes.string2bytes(key));
		return popObjectResponse(list);
	}

	public Boolean hmset(String key, Map<String, T> hash) {
		if (key == null || CollectionUtils.isEmpty(hash)) {
			return false;
		}

		Map<byte[], byte[]> map = new HashMap<byte[], byte[]>(hash.size(), 1);
		for (Entry<String, T> entry : hash.entrySet()) {
			String k = entry.getKey();
			if (k == null) {
				continue;
			}

			T v = entry.getValue();
			map.put(Bytes.string2bytes(k), v == null ? null : getSerializer().serialize(type, v));
		}

		return getBinaryOperations().hmset(Bytes.string2bytes(key), map);
	}

	public long incr(String key, long incr, long initValue) {
		Assert.notNull(key);
		return getStringOperations().incr(key, incr, initValue);
	}

	public long decr(String key, long decr, long initValue) {
		Assert.notNull(key);
		return getStringOperations().decr(key, decr, initValue);
	}

	public T getAndTouch(String key, int newExp) {
		if (key == null) {
			return null;
		}

		byte[] data = getBinaryOperations().getAndTouch(Bytes.string2bytes(key), newExp);
		return data == null ? null : getSerializer().deserialize(type, data);
	}

	public Map<String, T> mget(Collection<String> keys) {
		if (keys == null || keys.isEmpty()) {
			return null;
		}

		List<T> list = mget(keys.toArray(new String[keys.size()]));
		if (list == null || list.isEmpty()) {
			return null;
		}

		Map<String, T> map = new HashMap<String, T>(keys.size(), 1);
		Iterator<String> keyIterator = keys.iterator();
		Iterator<T> valueIterator = list.iterator();
		while (keyIterator.hasNext() && valueIterator.hasNext()) {
			String key = keyIterator.next();
			T value = valueIterator.next();
			if (key == null) {
				continue;
			}

			if (value == null) {
				continue;
			}

			map.put(key, value);
		}
		return map;
	}
}
