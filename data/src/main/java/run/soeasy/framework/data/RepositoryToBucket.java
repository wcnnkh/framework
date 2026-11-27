package run.soeasy.framework.data;

import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.streaming.Mapping;
import run.soeasy.framework.core.streaming.Streamable;

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

	@Override
	public Streamable<V> getValues(K key) {
		T entity = codec.encode(KeyValue.of(key, null));
		return repository.query(entity).map((e) -> codec.decode(e).getValue());
	}

	@Override
	public Mapping<K, V> batchQuery(@NonNull Streamable<K> keys) {
		return repository.batchQuery(keys.map((key) -> codec.encode(KeyValue.of(key, null))))
				.map((entity) -> codec.decode(entity)).toMapping(Function.identity());
	}

	@Override
	public Signal batchDelete(@NonNull Streamable<K> keys) {
		return repository.batchDelete(keys.map((key) -> codec.encode(KeyValue.of(key, null))));
	}

	@Override
	public Signal batchInsert(@NonNull Streamable<KeyValue<K, V>> keyValues) {
		return repository.batchInsert(keyValues.map((keyValue) -> codec.encode(keyValue)));
	}

	@Override
	public Signal batchUpdate(@NonNull Streamable<KeyValue<K, V>> keyValues) {
		return repository.batchUpdate(keyValues.map((keyValue) -> codec.encode(keyValue)));
	}
}
