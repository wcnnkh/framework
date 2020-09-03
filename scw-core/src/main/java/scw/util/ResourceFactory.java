package scw.util;

public interface ResourceFactory<T> {
	T getResource();

	void release(T resource);
}
