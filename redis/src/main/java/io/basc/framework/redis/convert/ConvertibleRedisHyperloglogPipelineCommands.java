package io.basc.framework.redis.convert;

import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisHyperloglogPipelineCommands;
import io.basc.framework.redis.RedisResponse;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisHyperloglogPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisHyperloglogPipelineCommands<K, V> {

	RedisHyperloglogPipelineCommands<SK, SV> getSourceRedisHyperloglogPipelineCommands();

	@Override
	default RedisResponse<Long> pfadd(K key, V... elements) {
		SK k = getKeyCodec().encode(key);
		SV[] tvs = getValueCodec().encode(elements);
		return getSourceRedisHyperloglogPipelineCommands().pfadd(k, tvs);
	}

	@Override
	default RedisResponse<Long> pfcount(K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisHyperloglogPipelineCommands().pfcount(ks);
	}

	@Override
	default RedisResponse<String> pfmerge(K destKey, K... sourceKeys) {
		SK dk = getKeyCodec().encode(destKey);
		SK[] sks = getKeyCodec().encode(sourceKeys);
		return getSourceRedisHyperloglogPipelineCommands().pfmerge(dk, sks);
	}
}