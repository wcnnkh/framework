package io.basc.framework.redis.convert;

import io.basc.framework.redis.RedisHyperloglogPipelineCommands;
import io.basc.framework.redis.RedisResponse;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisHyperloglogPipelineCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisHyperloglogPipelineCommands<K, V> {

	RedisHyperloglogPipelineCommands<SK, SV> getSourceRedisHyperloglogCommands();

	@Override
	default RedisResponse<Long> pfadd(K key, V... elements) {
		SK k = getKeyCodec().encode(key);
		SV[] tvs = getValueCodec().encodeAll(elements);
		return getSourceRedisHyperloglogCommands().pfadd(k, tvs);
	}

	@Override
	default RedisResponse<Long> pfcount(K... keys) {
		SK[] ks = getKeyCodec().encodeAll(keys);
		return getSourceRedisHyperloglogCommands().pfcount(ks);
	}

	@Override
	default RedisResponse<String> pfmerge(K destKey, K... sourceKeys) {
		SK dk = getKeyCodec().encode(destKey);
		SK[] sks = getKeyCodec().encodeAll(sourceKeys);
		return getSourceRedisHyperloglogCommands().pfmerge(dk, sks);
	}
}
