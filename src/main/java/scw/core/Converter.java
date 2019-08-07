package scw.core;

public interface Converter<K, V> {
	V convert(K k);
}
