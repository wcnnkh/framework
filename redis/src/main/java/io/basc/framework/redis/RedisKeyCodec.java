package io.basc.framework.redis;

import io.basc.framework.codec.Codec;

public interface RedisKeyCodec<SK, K> {
	Codec<K, SK> getKeyCodec();
}
