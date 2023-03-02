package io.basc.framework.redis;

import java.util.List;

@SuppressWarnings("unchecked")
public interface RedisTransactionsCommands<K, V> {
	String discard();

	List<Object> exec();

	RedisTransaction<K, V> multi();

	String unwatch();

	String watch(K... keys);
}
