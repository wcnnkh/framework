package scw.util;

import scw.lang.Ignore;

@Ignore
public interface KeyValuePair<K, V> {

	K getKey();

	V getValue();

}
