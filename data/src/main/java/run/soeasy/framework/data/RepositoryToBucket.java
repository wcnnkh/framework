package run.soeasy.framework.data;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Signal;

@Getter
@RequiredArgsConstructor
class RepositoryToBucket<K, V, T, R extends Repository<T>> implements Bucket<K, V> {
	@NonNull
	private final R repository;
	@NonNull
	private final Codec<KeyValue<K, V>, T> codec;

	@Override
	public Stream<KeyValue<K, V>> stream() {
		return repository.stream().map(codec::decode);
	}

	@Override
	public Signal delete(K key) {
		T entity = codec.encode(KeyValue.of(key, null));
		return repository.delete(entity);
	}

	@Override
	public Signal update(K key, V value) {
		T entity = codec.encode(KeyValue.of(key, value));
		return repository.update(entity);
	}

	@Override
	public Signal insert(K key, V value) {
		T entity = codec.encode(KeyValue.of(key, value));
		return repository.insert(entity);
	}
}
