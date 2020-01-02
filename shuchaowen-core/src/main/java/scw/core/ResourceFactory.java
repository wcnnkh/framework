package scw.core;

public interface ResourceFactory<T> {
	T getResource();

	void release(T resource);
}
