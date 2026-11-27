package run.soeasy.framework.data;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.streaming.Streamable;

public interface Repository<T> extends Streamable<T> {
	default Signal batchDelete(@NonNull Streamable<T> entities) {
		return Signal.batch(entities.map((entity) -> delete(entity)));
	}

	default Signal batchInsert(@NonNull Streamable<T> entities) {
		return Signal.batch(entities.map((entity) -> insert(entity)));
	}

	default Streamable<T> batchQuery(@NonNull Streamable<T> entities) {
		return entities.flatMap((entity) -> query(entity).stream());
	}

	default Signal batchUpdate(@NonNull Streamable<T> entities) {
		return Signal.batch(entities.map((entity) -> update(entity)));
	}

	Signal delete(@NonNull T entity);

	Signal insert(@NonNull T entity);

	Streamable<T> query(@NonNull T entity);

	default <K, V> Bucket<K, V> toBucket(@NonNull Codec<KeyValue<K, V>, T> codec) {
		return new RepositoryToBucket<K, V, T, Repository<T>>(this, codec);
	}

	Signal update(@NonNull T entity);
}
