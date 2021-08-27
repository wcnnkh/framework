package io.basc.framework.mapper;

import java.util.Map;

public interface ToMap<K, V> {
	Map<K, V> toMap();
}
