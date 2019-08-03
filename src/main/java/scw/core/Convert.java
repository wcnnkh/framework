package scw.core;

public interface Convert<K, V> {
	V convert(K k);
}
