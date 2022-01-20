package io.basc.framework.redis;

public interface RedisCodec<SK, K, SV, V> extends RedisKeyCodec<SK, K>, RedisValueCodec<SV, V> {
}
