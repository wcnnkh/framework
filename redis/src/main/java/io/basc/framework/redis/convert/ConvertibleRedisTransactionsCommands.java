package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.redis.RedisKeyCodec;
import io.basc.framework.redis.RedisTransactionsCommands;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisTransactionsCommands<SK, K>
		extends RedisKeyCodec<SK, K>, RedisTransactionsCommands<K> {

	RedisTransactionsCommands<SK> getSourceRedisTransactionsCommands();

	@Override
	default String discard() {
		return getSourceRedisTransactionsCommands().discard();
	}

	@Override
	default List<Object> exec() {
		return getSourceRedisTransactionsCommands().exec();
	}

	@Override
	default void multi() {
		getSourceRedisTransactionsCommands().multi();
	}

	@Override
	default String unwatch() {
		return getSourceRedisTransactionsCommands().unwatch();
	}

	@Override
	default String watch(K... keys) {
		return getSourceRedisTransactionsCommands().watch(getKeyCodec().encode(keys));
	}
}
