package scw.core;

import scw.core.annotation.Ignore;

@Ignore
public interface Converter<K, V> {
	V convert(K k);
}
