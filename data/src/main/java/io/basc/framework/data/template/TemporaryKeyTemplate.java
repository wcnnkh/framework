package io.basc.framework.data.template;

import io.basc.framework.data.kv.KeyOperations;

public interface TemporaryKeyTemplate<K> extends KeyOperations<K> {
	boolean touch(K key);
}
