package scw.util;

public interface ResourcePool<T> {
	T getResource();

	void release(T resource);
}
