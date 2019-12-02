package scw.core;

import scw.util.KeyValuePair;

public interface KeyValuePairFilter<K, V> {
	KeyValuePair<K, V> filter(KeyValuePair<K, V> pair);
}
