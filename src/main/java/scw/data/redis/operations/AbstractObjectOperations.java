package scw.data.redis.operations;

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

import scw.core.utils.Assert;
import scw.core.utils.CollectionUtils;
import scw.data.redis.RedisOperations;
import scw.io.serializer.Serializer;

public abstract class AbstractObjectOperations implements RedisOperations<String, Object> {
	protected abstract RedisOperations<byte[], byte[]> getBinaryOperations();

	public abstract byte[] string2bytes(String key);

	protected byte[][] string2bytes(String... keys) {
		byte[][] ks = new byte[keys.length][];
		for (int i = 0; i < keys.length; i++) {
			ks[i] = string2bytes(keys[i]);
		}
		return ks;
	}

	public abstract String bytes2string(byte[] bytes);

	public abstract Serializer getSerializer();

	public Object get(String key) {
		byte[] data = getBinaryOperations().get(string2bytes(key));
		return data == null ? null : getSerializer().deserialize(data);
	}

	public List<Object> mget(String... keys) {
		if (keys == null) {
			return null;
		}

		byte[][] ks = string2bytes(keys);
		List<byte[]> list = getBinaryOperations().mget(ks);
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		List<Object> dataList = new ArrayList<Object>(keys.length);
		for (byte[] data : list) {
			dataList.add(data == null ? null : getSerializer().deserialize(data));
		}
		return dataList;
	}

	public void set(String key, Object value) {
		if (key == null) {
			return;
		}

		getBinaryOperations().set(string2bytes(key), getSerializer().serialize(value));
	}

	public boolean setnx(String key, Object value) {
		if (key == null) {
			return false;
		}

		return getBinaryOperations().setnx(string2bytes(key), getSerializer().serialize(value));
	}

	public void setex(String key, int seconds, Object value) {
		if (key == null) {
			return;
		}

		getBinaryOperations().setex(string2bytes(key), seconds, getSerializer().serialize(value));
	}

	public boolean exists(String key) {
		if (key == null) {
			return false;
		}

		return getBinaryOperations().exists(string2bytes(key));
	}

	public Long expire(String key, int seconds) {
		if (key == null) {
			return -1L;
		}

		return getBinaryOperations().expire(string2bytes(key), seconds);
	}

	public boolean del(String key) {
		if (key == null) {
			return false;
		}

		return getBinaryOperations().del(string2bytes(key));
	}

	public Long hset(String key, String field, Object value) {
		if (key == null || field == null || value == null) {
			return -1L;
		}

		return getBinaryOperations().hset(string2bytes(key), string2bytes(field), getSerializer().serialize(value));
	}

	public Long hsetnx(String key, String field, Object value) {
		if (key == null || field == null || value == null) {
			return -1L;
		}

		return getBinaryOperations().hsetnx(string2bytes(key), string2bytes(field), getSerializer().serialize(value));
	}

	public Long hdel(String key, String... fields) {
		if (key == null || fields == null) {
			return -1L;
		}

		return getBinaryOperations().hdel(string2bytes(key), string2bytes(fields));
	}

	public Long hlen(String key) {
		if (key == null) {
			return -1L;
		}

		return getBinaryOperations().hlen(string2bytes(key));
	}

	public Boolean hexists(String key, String field) {
		if (key == null || field == null) {
			return false;
		}

		return getBinaryOperations().hexists(string2bytes(key), string2bytes(field));
	}

	public Long ttl(String key) {
		if (key == null) {
			return -1L;
		}

		return getBinaryOperations().ttl(string2bytes(key));
	}

	public Long incr(String key) {
		if (key == null) {
			return -1L;
		}

		return getBinaryOperations().incr(string2bytes(key));
	}

	public Long decr(String key) {
		if (key == null) {
			return -1L;
		}

		return getBinaryOperations().decr(string2bytes(key));
	}

	public Collection<Object> hvals(String key) {
		if (key == null) {
			return null;
		}

		Collection<byte[]> list = getBinaryOperations().hvals(string2bytes(key));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		List<Object> dataList = new ArrayList<Object>(list.size());
		for (byte[] data : list) {
			dataList.add(data == null ? null : getSerializer().deserialize(data));
		}
		return dataList;
	}

	public Object hget(String key, String field) {
		if (key == null || field == null) {
			return null;
		}

		byte[] data = getBinaryOperations().hget(string2bytes(key), string2bytes(field));
		return data == null ? null : getSerializer().deserialize(data);
	}

	public Collection<Object> hmget(String key, String... fields) {
		if (key == null || fields == null) {
			return null;
		}

		Collection<byte[]> list = getBinaryOperations().hmget(string2bytes(key), string2bytes(fields));
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}

		List<Object> valueList = new ArrayList<Object>(list.size());
		for (byte[] data : list) {
			valueList.add(data == null ? null : getSerializer().deserialize(data));
		}
		return valueList;
	}

	private byte[][] objectsSerialize(Object... values) {
		byte[][] bs = new byte[values.length][];
		for (int i = 0; i < values.length; i++) {
			Object v = values[i];
			if (v == null) {
				continue;
			}

			bs[i] = getSerializer().serialize(v);
		}
		return bs;
	}

	public Long lpush(String key, Object... values) {
		if (key == null || values == null) {
			return -1L;
		}

		return getBinaryOperations().lpush(string2bytes(key), objectsSerialize(values));
	}

	public Long rpush(String key, Object... values) {
		if (key == null || values == null) {
			return -1L;
		}

		return getBinaryOperations().rpush(string2bytes(key), objectsSerialize(values));
	}

	public Object rpop(String key) {
		if (key == null) {
			return null;
		}

		byte[] data = getBinaryOperations().rpop(string2bytes(key));
		return data == null ? null : getSerializer().deserialize(data);
	}

	public Object lpop(String key) {
		if (key == null) {
			return null;
		}

		byte[] data = getBinaryOperations().lpop(string2bytes(key));
		return data == null ? null : getSerializer().deserialize(data);
	}

	public Set<Object> smembers(String key) {
		if (key == null) {
			return null;
		}

		Set<byte[]> set = getBinaryOperations().smembers(string2bytes(key));
		if (CollectionUtils.isEmpty(set)) {
			return null;
		}

		Set<Object> hashSet = new HashSet<Object>(set.size(), 1);
		Iterator<byte[]> iterator = set.iterator();
		while (iterator.hasNext()) {
			byte[] data = iterator.next();
			hashSet.add(data == null ? null : getSerializer().deserialize(data));
		}
		return hashSet;
	}

	public Long srem(String key, Object... members) {
		if (key == null || members == null) {
			return -1L;
		}

		return getBinaryOperations().srem(string2bytes(key), objectsSerialize(members));
	}

	public Long sadd(String key, Object... members) {
		if (key == null || members == null) {
			return -1L;
		}

		return getBinaryOperations().sadd(string2bytes(key), objectsSerialize(members));
	}

	public Long zadd(String key, long score, Object member) {
		if (key == null || member == null) {
			return -1L;
		}

		return getBinaryOperations().zadd(string2bytes(key), score, getSerializer().serialize(member));
	}

	public Boolean set(String key, Object value, String nxxx, String expx, long time) {
		if (key == null || value == null || nxxx == null || expx == null) {
			return false;
		}

		return getBinaryOperations().set(string2bytes(key), getSerializer().serialize(value), string2bytes(nxxx),
				string2bytes(expx), time);
	}

	public Boolean sIsMember(String key, Object member) {
		if (key == null || member == null) {
			return false;
		}

		return getBinaryOperations().sIsMember(string2bytes(key), getSerializer().serialize(member));
	}

	public Object lindex(String key, int index) {
		if (key == null) {
			return null;
		}

		byte[] data = getBinaryOperations().lindex(string2bytes(key), index);
		return data == null ? null : getSerializer().deserialize(data);
	}

	public Long llen(String key) {
		if (key == null) {
			return -1L;
		}
		return getBinaryOperations().llen(string2bytes(key));
	}

	public Object eval(String script, List<String> keys, List<Object> args) {
		if (script == null) {
			return null;
		}

		List<byte[]> ks = null;
		if (!CollectionUtils.isEmpty(keys)) {
			ks = new ArrayList<byte[]>(keys.size());
			Iterator<String> iterator = keys.iterator();
			while (iterator.hasNext()) {
				String v = iterator.next();
				ks.add(v == null ? null : string2bytes(v));
			}
		}

		List<byte[]> bs = null;
		if (!CollectionUtils.isEmpty(args)) {
			bs = new ArrayList<byte[]>(args.size());
			Iterator<Object> iterator = args.iterator();
			while (iterator.hasNext()) {
				Object v = iterator.next();
				bs.add(v == null ? null : getSerializer().serialize(v));
			}
		}

		return getBinaryOperations().eval(string2bytes(script), ks, bs);
	}

	private Map<String, Object> mapDeSerizale(Map<byte[], byte[]> map) {
		if (CollectionUtils.isEmpty(map)) {
			return null;
		}

		Map<String, Object> valueMap = new LinkedHashMap<String, Object>(map.size(), 1);
		for (Entry<byte[], byte[]> entry : map.entrySet()) {
			byte[] data = entry.getValue();
			valueMap.put(bytes2string(entry.getKey()), data == null ? null : getSerializer().deserialize(data));
		}
		return valueMap;
	}

	public Map<String, Object> hgetAll(String key) {
		if (key == null) {
			return null;
		}

		Map<byte[], byte[]> map = getBinaryOperations().hgetAll(string2bytes(key));
		return mapDeSerizale(map);
	}

	private List<Object> popObjectResponse(List<byte[]> list) {
		int i = 0;
		byte[] v1 = list.get(i++);
		if (v1 != null) {
			return null;
		}

		List<Object> valueList = new ArrayList<Object>(list.size());
		for (int size = list.size(); i < size; i++) {
			byte[] v = list.get(i);
			if (v == null) {
				continue;
			}

			valueList.add(getSerializer().deserialize(v));
		}
		return valueList;
	}

	public List<Object> brpop(int timeout, String key) {
		if (key == null) {
			return null;
		}

		List<byte[]> list = getBinaryOperations().brpop(timeout, string2bytes(key));
		return popObjectResponse(list);
	}

	public List<Object> blpop(int timeout, String key) {
		if (key == null) {
			return null;
		}

		List<byte[]> list = getBinaryOperations().blpop(timeout, string2bytes(key));
		return popObjectResponse(list);
	}

	public Boolean hmset(String key, Map<String, Object> hash) {
		if (key == null || CollectionUtils.isEmpty(hash)) {
			return false;
		}

		Map<byte[], byte[]> map = new HashMap<byte[], byte[]>(hash.size(), 1);
		for (Entry<String, Object> entry : hash.entrySet()) {
			String k = entry.getKey();
			if (k == null) {
				continue;
			}

			Object v = entry.getValue();
			map.put(string2bytes(key), v == null ? null : getSerializer().serialize(v));
		}

		return getBinaryOperations().hmset(string2bytes(key), map);
	}

	public long incr(String key, long incr, long initValue) {
		Assert.notNull(key);
		return getBinaryOperations().incr(string2bytes(key), incr, initValue);
	}

	public long decr(String key, long decr, long initValue) {
		Assert.notNull(key);
		return getBinaryOperations().decr(string2bytes(key), decr, initValue);
	}

	public Object getAndTouch(String key, int newExp) {
		if (key == null) {
			return null;
		}

		byte[] data = getBinaryOperations().getAndTouch(string2bytes(key), newExp);
		return data == null ? null : getSerializer().deserialize(data);
	}

	public Map<String, Object> mget(Collection<String> keys) {
		if (keys == null || keys.isEmpty()) {
			return null;
		}

		List<Object> list = mget(keys.toArray(new String[keys.size()]));
		if (list == null || list.isEmpty()) {
			return null;
		}

		Map<String, Object> map = new HashMap<String, Object>(keys.size(), 1);
		Iterator<String> keyIterator = keys.iterator();
		Iterator<Object> valueIterator = list.iterator();
		while (keyIterator.hasNext() && valueIterator.hasNext()) {
			String key = keyIterator.next();
			Object value = valueIterator.next();
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
