package run.soeasy.framework.util.collections;

/**
 * 获取所有的key
 * 
 * @author shuchaowen
 *
 * @param <K>
 */
public interface Keys<K> {
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
