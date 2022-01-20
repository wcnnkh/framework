package io.basc.framework.redis.convert;

import io.basc.framework.redis.RedisCodec;
import io.basc.framework.redis.RedisHyperloglogCommands;

@SuppressWarnings("unchecked")
public interface ConvertibleRedisHyperloglogCommands<SK, K, SV, V>
		extends RedisCodec<SK, K, SV, V>, RedisHyperloglogCommands<K, V> {
	RedisHyperloglogCommands<SK, SV> getSourceRedisHyperloglogCommands();

	@Override
	default Long pfadd(K key, V... elements) {
		SK k = getKeyCodec().encode(key);
		SV[] tvs = getValueCodec().encode(elements);
		return getSourceRedisHyperloglogCommands().pfadd(k, tvs);
	}

	@Override
	default Long pfcount(K... keys) {
		SK[] ks = getKeyCodec().encode(keys);
		return getSourceRedisHyperloglogCommands().pfcount(ks);
	}

	@Override
	default String pfmerge(K destKey, K... sourceKeys) {
		SK dk = getKeyCodec().encode(destKey);
		SK[] sks = getKeyCodec().encode(sourceKeys);
		return getSourceRedisHyperloglogCommands().pfmerge(dk, sks);
	}
}
