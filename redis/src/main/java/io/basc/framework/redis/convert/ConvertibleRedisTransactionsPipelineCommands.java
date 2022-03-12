package io.basc.framework.redis.convert;

import java.util.List;

import io.basc.framework.redis.RedisResponse;
import io.basc.framework.redis.RedisTransaction;
import io.basc.framework.redis.RedisTransactionsPipelineCommands;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisTransactionsPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisTransactionsPipelineCommands<K, V> {

	RedisTransactionsPipelineCommands<SK, SV> getSourceRedisTransactionsCommands();

	@Override
	default RedisResponse<String> discard() {
		return getSourceRedisTransactionsCommands().discard();
	}

	@Override
	default RedisResponse<List<Object>> exec() {
		return getSourceRedisTransactionsCommands().exec();
	}

	@Override
	default RedisTransaction<K, V> multi() {
		RedisTransaction<SK, SV> transaction = getSourceRedisTransactionsCommands().multi();
		return new DefaultConvertibleRedisTransactionsCommands<>(transaction, getKeyCodec(), getValueCodec());
	}

	@Override
	default RedisResponse<String> unwatch() {
		return getSourceRedisTransactionsCommands().unwatch();
	}

	@Override
	default RedisResponse<String> watch(K... keys) {
		return getSourceRedisTransactionsCommands().watch(getKeyCodec().encodeAll(keys));
	}
}
