package shuchaowen.core.cache;

public interface Memcached {
	<T> T get(String key);
}
