package run.soeasy.framework.data;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.streaming.Mapping;

public interface Bucket<K, V> extends Mapping<K, V> {
	Signal delete(K key);

	Signal update(K key, V value);

	Signal insert(K key, V value);

	default Repository<V> toRepository(@NonNull Codec<V, K> keyCodec) {
		return toRepository(keyCodec, Codec.identity());
	}

	default <T> Repository<T> toRepository(@NonNull Codec<T, K> keyCodec, @NonNull Codec<T, V> valueCode) {
		return new BucketToRepository<>(this, keyCodec, valueCode);
	}
}