package scw.data.redis;

import java.nio.charset.Charset;
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

import scw.core.io.Bytes;
import scw.core.serializer.NoTypeSpecifiedSerializer;
import scw.core.utils.Assert;
import scw.core.utils.CollectionUtils;

public abstract class AbstractRedis implements Redis {
	private final RedisOperations<String, Object> objectOperations = new ObjectOperations();

	protected abstract NoTypeSpecifiedSerializer getSerializer();

	protected abstract Charset getCharset();

	public RedisOperations<String, Object> getObjectOperations() {
		return objectOperations;
	}

	private final class ObjectOperations implements RedisOperations<String, Object> {

		public Object get(String key) {
			byte[] data = getBinaryOperations().get(key.getBytes(getCharset()));
			if (data == null) {
				return null;
			}

			return getSerializer().deserialize(data);
		}

		public List<Object> mget(String... keys) {
			if (keys == null) {
				return null;
			}

			byte[][] ks = Bytes.string2bytes(getCharset(), keys);
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

		public Boolean set(String key, Object value) {
			if (key == null || value == null) {
				return false;
			}

			return getBinaryOperations().set(key.getBytes(getCharset()), getSerializer().serialize(value));
		}

		public long setnx(String key, Object value) {
			if (key == null || value == null) {
				return 0;
			}

			return getBinaryOperations().setnx(key.getBytes(getCharset()), getSerializer().serialize(value));
		}

		public Boolean setex(String key, int seconds, Object value) {
			if (key == null || value == null) {
				return false;
			}

			return getBinaryOperations().setex(key.getBytes(getCharset()), seconds, getSerializer().serialize(value));
		}

		public Boolean exists(String key) {
			if (key == null) {
				return false;
			}

			return getBinaryOperations().exists(key.getBytes(getCharset()));
		}

		public Long expire(String key, int seconds) {
			if (key == null) {
				return -1L;
			}

			return getBinaryOperations().expire(key.getBytes(getCharset()), seconds);
		}

		public Long del(String key) {
			if (key == null) {
				return -1L;
			}

			return getBinaryOperations().del(key.getBytes(getCharset()));
		}

		public Long hset(String key, String field, Object value) {
			if (key == null || field == null || value == null) {
				return -1L;
			}

			return getBinaryOperations().hset(key.getBytes(getCharset()), field.getBytes(getCharset()),
					getSerializer().serialize(value));
		}

		public Long hsetnx(String key, String field, Object value) {
			if (key == null || field == null || value == null) {
				return -1L;
			}

			return getBinaryOperations().hsetnx(key.getBytes(getCharset()), field.getBytes(getCharset()),
					getSerializer().serialize(value));
		}

		public Long hdel(String key, String... fields) {
			if (key == null || fields == null) {
				return -1L;
			}

			return getBinaryOperations().hdel(key.getBytes(getCharset()), Bytes.string2bytes(getCharset(), fields));
		}

		public Long hlen(String key) {
			if (key == null) {
				return -1L;
			}

			return getBinaryOperations().hlen(key.getBytes(getCharset()));
		}

		public Boolean hexists(String key, String field) {
			if (key == null || field == null) {
				return false;
			}

			return getBinaryOperations().hexists(key.getBytes(getCharset()), field.getBytes(getCharset()));
		}

		public Long ttl(String key) {
			if (key == null) {
				return -1L;
			}

			return getBinaryOperations().ttl(key.getBytes(getCharset()));
		}

		public Long incr(String key) {
			if (key == null) {
				return -1L;
			}

			return getBinaryOperations().incr(key.getBytes(getCharset()));
		}

		public Long decr(String key) {
			if (key == null) {
				return -1L;
			}

			return getBinaryOperations().decr(key.getBytes(getCharset()));
		}

		public Collection<Object> hvals(String key) {
			if (key == null) {
				return null;
			}

			Collection<byte[]> list = getBinaryOperations().hvals(key.getBytes(getCharset()));
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

			byte[] data = getBinaryOperations().hget(key.getBytes(getCharset()), field.getBytes(getCharset()));
			return data == null ? null : getSerializer().deserialize(data);
		}

		public Collection<Object> hmget(String key, String... fields) {
			if (key == null || fields == null) {
				return null;
			}

			Collection<byte[]> list = getBinaryOperations().hmget(key.getBytes(),
					Bytes.string2bytes(getCharset(), fields));
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

			return getBinaryOperations().lpush(key.getBytes(getCharset()), objectsSerialize(values));
		}

		public Long rpush(String key, Object... values) {
			if (key == null || values == null) {
				return -1L;
			}

			return getBinaryOperations().rpush(key.getBytes(getCharset()), objectsSerialize(values));
		}

		public Object rpop(String key) {
			if (key == null) {
				return null;
			}

			byte[] data = getBinaryOperations().rpop(key.getBytes(getCharset()));
			return data == null ? null : getSerializer().deserialize(data);
		}

		public Object lpop(String key) {
			if (key == null) {
				return null;
			}

			byte[] data = getBinaryOperations().lpop(key.getBytes(getCharset()));
			return data == null ? null : getSerializer().deserialize(data);
		}

		public Set<Object> smembers(String key) {
			if (key == null) {
				return null;
			}

			Set<byte[]> set = getBinaryOperations().smembers(key.getBytes(getCharset()));
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

			return getBinaryOperations().srem(key.getBytes(getCharset()), objectsSerialize(members));
		}

		public Long sadd(String key, Object... members) {
			if (key == null || members == null) {
				return -1L;
			}

			return getBinaryOperations().sadd(key.getBytes(getCharset()), objectsSerialize(members));
		}

		public Long zadd(String key, long score, Object member) {
			if (key == null || member == null) {
				return -1L;
			}

			return getBinaryOperations().zadd(key.getBytes(getCharset()), score, getSerializer().serialize(member));
		}

		public Boolean set(String key, Object value, String nxxx, String expx, long time) {
			if (key == null || value == null || nxxx == null || expx == null) {
				return false;
			}

			return getBinaryOperations().set(key.getBytes(getCharset()), getSerializer().serialize(value),
					nxxx.getBytes(getCharset()), expx.getBytes(getCharset()), time);
		}

		public Boolean sIsMember(String key, Object member) {
			if (key == null || member == null) {
				return false;
			}

			return getBinaryOperations().sIsMember(key.getBytes(getCharset()), getSerializer().serialize(member));
		}

		public Object lindex(String key, int index) {
			if (key == null) {
				return null;
			}

			byte[] data = getBinaryOperations().lindex(key.getBytes(getCharset()), index);
			return data == null ? null : getSerializer().deserialize(data);
		}

		public Long llen(String key) {
			if (key == null) {
				return -1L;
			}
			return getBinaryOperations().llen(key.getBytes(getCharset()));
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
					ks.add(v == null ? null : v.getBytes(getCharset()));
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

			return getBinaryOperations().eval(script.getBytes(getCharset()), ks, bs);
		}

		private Map<String, Object> mapDeSerizale(Map<byte[], byte[]> map) {
			if (CollectionUtils.isEmpty(map)) {
				return null;
			}

			Map<String, Object> valueMap = new LinkedHashMap<String, Object>(map.size(), 1);
			for (Entry<byte[], byte[]> entry : map.entrySet()) {
				byte[] data = entry.getValue();
				valueMap.put(new String(entry.getKey(), getCharset()),
						data == null ? null : getSerializer().deserialize(data));
			}
			return valueMap;
		}

		private List<Object> listDeSerizale(Collection<byte[]> list) {
			if (CollectionUtils.isEmpty(list)) {
				return null;
			}

			List<Object> valueList = new ArrayList<Object>();
			for (byte[] data : list) {
				valueList.add(data == null ? null : getSerializer().deserialize(data));
			}
			return valueList;
		}

		public Map<String, Object> hgetAll(String key) {
			if (key == null) {
				return null;
			}

			Map<byte[], byte[]> map = getBinaryOperations().hgetAll(key.getBytes(getCharset()));
			return mapDeSerizale(map);
		}

		public List<Object> brpop(int timeout, String key) {
			if (key == null) {
				return null;
			}

			List<byte[]> list = getBinaryOperations().brpop(timeout, key.getBytes(getCharset()));
			return listDeSerizale(list);
		}

		public List<Object> blpop(int timeout, String key) {
			if (key == null) {
				return null;
			}

			List<byte[]> list = getBinaryOperations().blpop(timeout, key.getBytes(getCharset()));
			if (list == null || list.size() != 0) {
				return null;
			}

			List<Object> valueList = new ArrayList<Object>(list.size());
			byte[] v1 = list.get(0);
			byte[] v2 = list.get(1);
			if (v1 == null) {
				valueList.add(null);
				valueList.add(Bytes.bytes2int(v2));
			} else {
				valueList.add(new String(v1, getCharset()));
				valueList.add(v2 == null ? null : getSerializer().deserialize(v2));
			}
			return valueList;
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
				map.put(k.getBytes(getCharset()), v == null ? null : getSerializer().serialize(v));
			}

			return getBinaryOperations().hmset(key.getBytes(getCharset()), map);
		}

		public long incr(String key, long incr, long initValue) {
			Assert.notNull(key);
			return getBinaryOperations().incr(key.getBytes(getCharset()), incr, initValue);
		}

		public long decr(String key, long decr, long initValue) {
			Assert.notNull(key);
			return getBinaryOperations().decr(key.getBytes(getCharset()), decr, initValue);
		}

		public Object getAndTouch(String key, int newExp) {
			if (key == null) {
				return null;
			}

			return getBinaryOperations().getAndTouch(key.getBytes(getCharset()), newExp);
		}

	}
}
