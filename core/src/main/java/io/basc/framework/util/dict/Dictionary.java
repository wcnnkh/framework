package io.basc.framework.util.dict;

import java.util.NoSuchElementException;

import io.basc.framework.util.KeyValues;
import io.basc.framework.util.NoUniqueElementException;

/**
 * 字典
 * 
 * @author shuchaowen
 *
 * @param <K>
 * @param <V>
 */
public interface Dictionary<K, V> extends KeyValues<K, V> {
	/**
	 * 获取唯一的值
	 * 
	 * @param key
	 * @return
	 * @throws NoSuchElementException
	 * @throws NoUniqueElementException
	 */
	default V getUniqueValue(K key) throws NoSuchElementException, NoUniqueElementException {
		return getElements(key).getUnique().getValue();
	}

	/**
	 * 是否有唯一的值
	 * 
	 * @param key
	 * @return
	 */
	default boolean hasUniqueValue(K key) {
		return getElements(key).isUnique();
	}
}
