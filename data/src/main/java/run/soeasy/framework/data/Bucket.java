package run.soeasy.framework.data;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.streaming.Mapping;
import run.soeasy.framework.core.streaming.Streamable;

public interface Bucket<K, V> extends Mapping<K, V> {
	default Signal batchDelete(@NonNull Streamable<K> keys) {
		return Signal.batch(keys.map((key) -> delete(key)));
	}

	default Signal batchInsert(@NonNull Streamable<KeyValue<K, V>> keyValues) {
		return Signal.batch(keyValues.map((keyValue) -> insert(keyValue.getKey(), keyValue.getValue())));
	}

	default Mapping<K, V> batchQuery(@NonNull Streamable<K> keys) {
		return keys.flatMap((key) -> getValues(key).map((value) -> KeyValue.of(key, value)).stream())
				.toMapping(Function.identity());
	}

	default Signal batchUpdate(@NonNull Streamable<KeyValue<K, V>> keyValues) {
		return Signal.batch(keyValues.map((keyValue) -> update(keyValue.getKey(), keyValue.getValue())));
	}

	Signal delete(K key);

	@Override
	Streamable<V> getValues(K key);

	Signal insert(K key, V value);

	@Override
	Stream<KeyValue<K, V>> stream();

	default <T> Repository<T> toRepository(@NonNull Codec<T, KeyValue<K, V>> codec) {
		return new BucketToRepository<>(this, codec);
	}

	Signal update(K key, V value);
}