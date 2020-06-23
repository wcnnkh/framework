package scw.core;

import scw.lang.Ignore;

@Ignore
public interface Converter<K, V> {
	V convert(K k);
}
