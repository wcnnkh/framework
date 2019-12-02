package scw.util;

import scw.core.annotation.Ignore;

@Ignore
public interface KeyValuePair<K, V> {

	K getKey();

	V getValue();

}
