package io.basc.framework.convert.transform;

import io.basc.framework.convert.TypeDescriptor;
import lombok.NonNull;

@FunctionalInterface
public interface MappingStrategyFactory<M extends Mapping<K, ? extends V>, K, V extends Accessor, E extends Throwable> {
	MappingStrategy<M, K, V, E> getMappingStrategy(@NonNull TypeDescriptor targetType);
}
