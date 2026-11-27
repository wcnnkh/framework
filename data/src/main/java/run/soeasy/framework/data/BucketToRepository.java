package run.soeasy.framework.data;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.streaming.Streamable;

@Getter
@RequiredArgsConstructor
class BucketToRepository<T, K, V, B extends Bucket<K, V>> implements Repository<T> {
	@NonNull
	private final B bucket;
	@NonNull
	private final Codec<T, KeyValue<K, V>> codec;

	@Override
	public Stream<T> stream() {
		return bucket.stream().map((e) -> codec.decode(e));
	}

	@Override
	public Streamable<T> query(T entity) {
		K key = codec.encode(entity).getKey();
		return bucket.getValues(key).map((value) -> codec.decode(KeyValue.of(key, value)));
	}

	@Override
	public Signal delete(T entity) {
		K key = codec.encode(entity).getKey();
		return bucket.delete(key);
	}

	@Override
	public Signal update(T entity) {
		KeyValue<K, V> keyValue = codec.encode(entity);
		return bucket.update(keyValue.getKey(), keyValue.getValue());
	}

	@Override
	public Signal insert(T entity) {
		KeyValue<K, V> keyValue = codec.encode(entity);
		return bucket.insert(keyValue.getKey(), keyValue.getValue());
	}

	@Override
	public Signal batchDelete(@NonNull Streamable<T> entities) {
		return bucket.batchDelete(entities.map((entity) -> codec.encode(entity).getKey()));
	}

	@Override
	public Signal batchInsert(@NonNull Streamable<T> entities) {
		return bucket.batchInsert(entities.map((entity) -> codec.encode(entity)));
	}

	@Override
	public Streamable<T> batchQuery(@NonNull Streamable<T> entities) {
		return bucket.batchQuery(entities.map((entity) -> codec.encode(entity).getKey()))
				.map((keyValue) -> codec.decode(keyValue));
	}

	@Override
	public Signal batchUpdate(@NonNull Streamable<T> entities) {
		return bucket.batchUpdate(entities.map((entity) -> codec.encode(entity)));
	}
}
