package io.basc.framework.data.kv;

import java.util.concurrent.TimeUnit;

public interface TemporaryKeyOperations<K> extends KeyOperations<K> {
	/**
	 * touch and expire
	 * 
	 * @param key
	 * @param exp
	 * @param expUnit
	 * @return
	 */
	boolean touch(K key, long exp, TimeUnit expUnit);

	/**
	 * 设置过期时间
	 * 
	 * @param key
	 * @param exp
	 * @param expUnit
	 * @return
	 */
	boolean expire(K key, long exp, TimeUnit expUnit);
}
