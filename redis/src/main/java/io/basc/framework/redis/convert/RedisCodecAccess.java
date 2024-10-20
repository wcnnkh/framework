package io.basc.framework.redis.convert;

import io.basc.framework.codec.Codec;
import io.basc.framework.core.reflect.ReflectionUtils;
import io.basc.framework.util.Assert;

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
			return (T) ReflectionUtils.clone(this);
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
