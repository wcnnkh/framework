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
	Elements<V> getValues(K key);
}
