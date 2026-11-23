package run.soeasy.framework.data;

import java.util.stream.Stream;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.streaming.Streamable;

@Getter
@RequiredArgsConstructor
class BucketToRepository<T, K, V, B extends Bucket<K, V>> implements Repository<T> {
	@NonNull
	private final B bucket;
	@NonNull
	private final Codec<T, K> keyCodec;
	@NonNull
	private final Codec<T, V> valueCodec;

	@Override
	public Stream<T> stream() {
		return bucket.stream().map((e) -> valueCodec.decode(e.getValue()));
	}

	@Override
	public Streamable<T> query(T entity) {
		K key = keyCodec.encode(entity);
		return bucket.getValues(key).map(valueCodec::decode);
	}

	@Override
	public Signal delete(T entity) {
		K key = keyCodec.encode(entity);
		return bucket.delete(key);
	}

	@Override
	public Signal update(T entity) {
		K key = keyCodec.encode(entity);
		V value = valueCodec.encode(entity);
		return bucket.update(key, value);
	}

	@Override
	public Signal insert(T entity) {
		K key = keyCodec.encode(entity);
		V value = valueCodec.encode(entity);
		return bucket.insert(key, value);
	}

}
