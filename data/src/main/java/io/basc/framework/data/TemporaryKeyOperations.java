package io.basc.framework.data;

import java.util.concurrent.TimeUnit;

public interface TemporaryKeyOperations<K> extends KeyOperations<K> {
	boolean touch(K key, long exp, TimeUnit expUnit);

	boolean expire(K key, long exp, TimeUnit expUnit);
}
