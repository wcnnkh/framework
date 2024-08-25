package io.basc.framework.util;

import java.io.Serializable;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class StandardKeyValue<K, V> implements KeyValue<K, V>, Serializable {
	private static final long serialVersionUID = 1L;
	private final K key;
	private final V value;
}
