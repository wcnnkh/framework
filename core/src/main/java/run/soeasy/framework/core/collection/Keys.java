package run.soeasy.framework.core.collection;

import run.soeasy.framework.core.domain.Wrapper;

/**
 * 获取所有的key
 * 
 * @author shuchaowen
 *
 * @param <K>
 */
public interface Keys<K> {
	public static interface KeysWrapper<K, W extends Keys<K>> extends Keys<K>, Wrapper<W> {
		@Override
		default Elements<K> keys() {
			return getSource().keys();
		}

		@Override
		default boolean hasKey(K key) {
			return getSource().hasKey(key);
		}
	}

	Elements<K> keys();

	/**
	 * 是否存在此key
	 * 
	 * @param key
	 * @return
	 */
	default boolean hasKey(K key) {
		return keys().contains(key);
	}
}
