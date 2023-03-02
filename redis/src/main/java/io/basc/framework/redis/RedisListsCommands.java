package io.basc.framework.redis;

import java.util.List;

@SuppressWarnings("unchecked")
public interface RedisListsCommands<K, V> {
	V blmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to, long timout);

	default List<V> blpop(K... keys) {
		return blpop((double) 0, keys);
	}

	List<V> blpop(double timeout, K... keys);

	default List<V> brpop(K... keys) {
		return brpop((double) 0, keys);
	}

	List<V> brpop(double timeout, K... keys);

	V brpoplpush(K sourceKey, K destinationKey, double timout);

	V lindex(K key, long index);

	Long linsert(K key, InsertPosition position, V pivot, V value);

	Long llen(K key);

	V lmove(K sourceKey, K destinationKey, MovePosition from, MovePosition to);

	List<V> lpop(K key, int count);

	Long lpush(K key, V... elements);

	Long lpushx(K key, V... elements);

	List<V> lrange(K key, long start, long stop);

	Long lrem(K key, int count, V element);

	Boolean lset(K key, long index, V element);

	Boolean ltrim(K key, long start, long stop);

	List<V> rpop(K key, int count);

	V rpoplpush(K sourceKey, K destinationKey);

	Long rpush(K key, V... elements);

	Long rpushx(K key, V... elements);
}
