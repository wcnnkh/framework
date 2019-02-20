package scw.sql.orm.cache;

import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import scw.beans.BeanFieldListen;
import scw.memcached.Memcached;

public class MemcachedCache implements Cache {
	private final Memcached memcached;
	private final int exp;

	public MemcachedCache(Memcached memcached, int exp) {
		this.memcached = memcached;
		this.exp = exp;
	}

	public <T> T get(Class<T> type, String key) {
		T t = memcached.getAndTocuh(key, exp);
		if (t == null) {
			return null;
		}

		if (t instanceof BeanFieldListen) {
			((BeanFieldListen) t).start_field_listen();
		}
		return t;
	}

	public void delete(String key) {
		memcached.delete(key);
	}

	public void add(String key, Object data) {
		memcached.add(key, exp, data);
	}

	public void set(String key, Object data) {
		memcached.set(key, exp, data);
	}

	public <T> Map<String, T> getMap(Class<T> type, Collection<String> keys) {
		Map<String, T> map = memcached.get(keys);
		if (map != null && !map.isEmpty()) {
			for (Entry<String, T> entry : map.entrySet()) {
				T v = entry.getValue();
				if (v == null) {
					map.remove(entry.getKey());
					continue;
				}

				if (v instanceof BeanFieldListen) {
					((BeanFieldListen) v).start_field_listen();
				}
			}
		}
		return map;
	}
}
