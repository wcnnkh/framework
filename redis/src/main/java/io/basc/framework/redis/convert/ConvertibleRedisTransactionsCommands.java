package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.redis.RedisTransaction;
import io.basc.framework.redis.RedisTransactionsCommands;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisTransactionsCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisTransactionsCommands<K, V> {

	RedisTransactionsCommands<SK, SV> getSourceRedisTransactionsCommands();

	@Override
	default String discard() {
		return getSourceRedisTransactionsCommands().discard();
	}

	@Override
	default List<Object> exec() {
		return getSourceRedisTransactionsCommands().exec();
	}

	@Override
	default RedisTransaction<K, V> multi() {
		RedisTransaction<SK, SV> transaction = getSourceRedisTransactionsCommands().multi();
		return new DefaultConvertibleRedisTransactionsCommands<>(transaction, getKeyCodec(), getValueCodec());
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
