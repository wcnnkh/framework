package io.basc.framework.util.config;

public interface PropertyFactory<K, V> {
	V getProperty(K index);

	long size();

	default boolean isEmpty() {
		return size() == 0;
	}
}
