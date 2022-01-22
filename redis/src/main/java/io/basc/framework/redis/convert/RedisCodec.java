package io.basc.framework.redis.convert;

import io.basc.framework.codec.Codec;

public interface RedisCodec<SK, K, SV, V> {
	Codec<K, SK> getKeyCodec();

	Codec<V, SV> getValueCodec();
}
