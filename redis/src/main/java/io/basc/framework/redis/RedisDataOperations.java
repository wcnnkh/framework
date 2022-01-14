package io.basc.framework.redis;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.basc.framework.context.annotation.Provider;
import io.basc.framework.data.DataOperations;
import io.basc.framework.util.CollectionUtils;

@Provider
public class RedisDataOperations implements DataOperations {
	private final Redis redisTemplete;

	public RedisDataOperations(Redis redisTemplate) {
		this.redisTemplete = redisTemplate;
	}

	@Override
	public boolean touch(String key, long exp) {
		return redisTemplete.touch(key) == 1;
	}

	@Override
	public boolean add(String key, long exp, Object value) {
		if (exp > 0) {
			Boolean b = redisTemplete.getObjectCommands().set(key, value, ExpireOption.EX, exp, SetOption.NX);
			return b == null ? false : b;
		} else {
			redisTemplete.getObjectCommands().setNX(key, value);
			return true;
		}
	}

	@Override
	public void set(String key, long exp, Object value) {
		if (exp > 0) {
			redisTemplete.getObjectCommands().setex(key, exp, value);
		} else {
			redisTemplete.getObjectCommands().set(key, value);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T get(String key) {
		return (T) redisTemplete.get(key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> Map<String, T> get(Collection<String> keys) {
		List<Object> list = redisTemplete.getObjectCommands().mget(keys.toArray(new String[0]));
		if (CollectionUtils.isEmpty(list)) {
			return Collections.emptyMap();
		}

		Map<String, T> map = new LinkedHashMap<String, T>(list.size());
		Iterator<String> keyIterator = keys.iterator();
		Iterator<Object> valueIterator = list.iterator();
		while (keyIterator.hasNext() && valueIterator.hasNext()) {
			map.put(keyIterator.next(), (T) valueIterator.next());
		}
		return map;
	}

	@Override
	public boolean add(String key, Object value) {
		Boolean v = redisTemplete.getObjectCommands().setNX(key, value);
		return v == null ? false : v;
	}

	@Override
	public void set(String key, Object value) {
		redisTemplete.getObjectCommands().set(key, value);
	}

	@Override
	public boolean isExist(String key) {
		return redisTemplete.exists(key) == 1;
	}

	@Override
	public boolean delete(String key) {
		return redisTemplete.del(key) == 1;
	}

	@Override
	public void delete(Collection<String> keys) {
		redisTemplete.del(keys.toArray(new String[0]));
	}

	@Override
	public long incr(String key, long delta, long initialValue, long exp) {
		return redisTemplete.incr(key, delta, initialValue, exp);
	}

	@Override
	public long decr(String key, long delta, long initialValue, long exp) {
		return redisTemplete.decr(key, delta, initialValue, exp);
	}

	@Override
	public long incr(String key, long delta) {
		return redisTemplete.incrBy(key, delta);
	}

	@Override
	public long incr(String key, long delta, long initialValue) {
		return redisTemplete.incr(key, delta, initialValue, 0);
	}

	@Override
	public long decr(String key, long delta) {
		return redisTemplete.decrBy(key, delta);
	}

	@Override
	public long decr(String key, long delta, long initialValue) {
		return redisTemplete.decr(key, delta, initialValue, 0);
	}

}
