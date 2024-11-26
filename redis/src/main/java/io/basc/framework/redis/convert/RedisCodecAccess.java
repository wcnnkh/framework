package io.basc.framework.redis.convert;

import io.basc.framework.lang.UnsupportedException;
import io.basc.framework.util.Assert;
import io.basc.framework.util.codec.Codec;

public class RedisCodecAccess<SK, K, SV, V, T extends RedisCodecAccess<SK, K, SV, V, T>>
		implements RedisCodec<SK, K, SV, V>, Cloneable {
	protected Codec<K, SK> keyCodec;
	protected Codec<V, SV> valueCodec;

	public RedisCodecAccess() {
	}

	public RedisCodecAccess(Codec<K, SK> keyCodec, Codec<V, SV> valueCodec) {
		Assert.requiredArgument(keyCodec != null, "keyCodec");
		Assert.requiredArgument(valueCodec != null, "valueCodec");
		this.keyCodec = keyCodec;
		this.valueCodec = valueCodec;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T clone() {
		try {
			return (T) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new UnsupportedException(e);
		}
	}

	public Codec<K, SK> getKeyCodec() {
		return keyCodec;
	}

	public T setKeyCodec(Codec<K, SK> keyCodec) {
		Assert.requiredArgument(keyCodec != null, "keyCodec");
		T access = clone();
		access.keyCodec = keyCodec;
		return access;
	}

	public Codec<V, SV> getValueCodec() {
		return valueCodec;
	}

	public T setValueCodec(Codec<V, SV> valueCodec) {
		Assert.requiredArgument(valueCodec != null, "valueCodec");
		T access = clone();
		access.valueCodec = valueCodec;
		return access;
	}
}
