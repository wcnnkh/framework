package scw.data.redis.jedis;

public interface ResourceManager<T> {
	T getResource();

	void close(T resource);
}
