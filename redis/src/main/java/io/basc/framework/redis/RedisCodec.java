package io.basc.framework.redis;

import io.basc.framework.codec.Codec;

public interface RedisCodec<SK, K, SV, V> {
	Codec<SK, K> getKeyCodec();
	
	Codec<SV, V> getValueCodec();
}
