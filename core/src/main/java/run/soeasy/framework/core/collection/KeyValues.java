package run.soeasy.framework.core.collection;

/**
 * 多个键值对
 * 
 * @author soeasy.run
 *
 * @param <K>
 * @param <V>
 */
public interface KeyValues<K, V> extends Keys<K> {
	public static interface KeyValuesWrapper<K, V, W extends KeyValues<K, V>>
			extends KeyValues<K, V>, KeysWrapper<K, W> {
		@Override
		default Elements<V> getValues(K key) {
			return getSource().getValues(key);
		}
	}

	Elements<V> getValues(K key);
}
