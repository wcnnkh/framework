package io.basc.framework.redis;

import io.basc.framework.codec.Codec;

public interface RedisValueCodec<SV, V> {
	Codec<V, SV> getValueCodec();
}
