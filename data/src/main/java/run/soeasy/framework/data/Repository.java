package run.soeasy.framework.data;

import lombok.NonNull;
import run.soeasy.framework.codec.Codec;
import run.soeasy.framework.core.domain.KeyValue;
import run.soeasy.framework.core.exchange.Signal;
import run.soeasy.framework.core.streaming.Streamable;

public interface Repository<T> extends Streamable<T> {
	Streamable<T> query(@NonNull T entity);

	Signal delete(@NonNull T entity);

	Signal update(@NonNull T entity);

	Signal insert(@NonNull T entity);

	default <K, V> Bucket<K, V> toBucket(@NonNull Codec<KeyValue<K, V>, T> codec) {
		return new RepositoryToBucket<K, V, T, Repository<T>>(this, codec);
	}
}
