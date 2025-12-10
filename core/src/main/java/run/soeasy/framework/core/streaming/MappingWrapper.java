package run.soeasy.framework.core.streaming;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;

import lombok.NonNull;
import run.soeasy.framework.core.domain.KeyValue;

@FunctionalInterface
public interface MappingWrapper<K, V, W extends Mapping<K, V>>
		extends Mapping<K, V>, StreamableWrapper<KeyValue<K, V>, W> {

	@Override
	default Mapping<K, V> reload() {
		return getSource().reload();
	}

	@Override
	default Streamable<V> getValues(K key) {
		return getSource().getValues(key);
	}

	@Override
	default boolean hasKey(K key) {
		return getSource().hasKey(key);
	}
	
	@Override
	default Streamable<K> keys() {
		return getSource().keys();
	}

	@Override
	default Mapping<K, V> toIndexed() {
		return getSource().toIndexed();
	}

	@Override
	default Mapping<K, V> toIndexed(@NonNull Supplier<? extends List<KeyValue<K, V>>> collectionFactory) {
		return getSource().toIndexed(collectionFactory);
	}

	@Override
	default Mapping<K, V> toMapped() {
		return getSource().toMapped();
	}

	@Override
	default Mapping<K, V> toMapped(@NonNull BinaryOperator<V> mergeFunction) {
		return getSource().toMapped(mergeFunction);
	}

	@Override
	default Mapping<K, V> toMapped(@NonNull BinaryOperator<V> mergeFunction,
			@NonNull Supplier<Map<K, V>> mapFactory) {
		return getSource().toMapped(mergeFunction, mapFactory);
	}

	@Override
	default Mapping<K, V> toMultiMapped() {
		return getSource().toMultiMapped();
	}

	@Override
	default Mapping<K, V> toMultiMapped(@NonNull Supplier<Map<K, Collection<V>>> mapFactory) {
		return getSource().toMultiMapped(mapFactory);
	}

	@Override
	default Mapping<K, V> toMultiMapped(@NonNull Supplier<Map<K, Collection<V>>> mapFactory,
			@NonNull Supplier<Collection<V>> collectionFactory) {
		return getSource().toMultiMapped(mapFactory, collectionFactory);
	}

	@Override
	default boolean isMapped() {
		return getSource().isMapped();
	}
}
