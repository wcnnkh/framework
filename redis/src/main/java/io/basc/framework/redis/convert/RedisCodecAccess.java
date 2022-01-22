package io.basc.framework.redis.convert;

import io.basc.framework.codec.Codec;
import io.basc.framework.util.Assert;

public class RedisCodecAccess<SK, K, SV, V> implements RedisCodec<SK, K, SV, V> {
	private Codec<K, SK> keyCodec;
	private Codec<V, SV> valueCodec;

	public RedisCodecAccess() {
	}

	public RedisCodecAccess(Codec<K, SK> keyCodec, Codec<V, SV> valueCodec) {
		Assert.requiredArgument(keyCodec != null, "keyCodec");
		Assert.requiredArgument(valueCodec != null, "valueCodec");
		this.keyCodec = keyCodec;
		this.valueCodec = valueCodec;
	}

	public Codec<K, SK> getKeyCodec() {
		return keyCodec;
	}

	public void setKeyCodec(Codec<K, SK> keyCodec) {
		Assert.requiredArgument(keyCodec != null, "keyCodec");
		this.keyCodec = keyCodec;
	}

	public Codec<V, SV> getValueCodec() {
		return valueCodec;
	}

	public void setValueCodec(Codec<V, SV> valueCodec) {
		Assert.requiredArgument(valueCodec != null, "valueCodec");
		this.valueCodec = valueCodec;
	}

}
