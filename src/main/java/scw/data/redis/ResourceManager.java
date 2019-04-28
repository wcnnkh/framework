package scw.data.redis;

public interface ResourceManager<T> {
	T getResource();

	void close(T resource);
}
